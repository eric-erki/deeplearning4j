/*
 *  ******************************************************************************
 *  * Copyright (c) 2021 Deeplearning4j Contributors
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *****************************************************************************
 */

package org.datavec.spark.inference.server;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.datavec.api.transform.TransformProcess;
import org.datavec.image.transform.ImageTransformProcess;
import org.datavec.spark.inference.model.CSVSparkTransform;
import org.datavec.spark.inference.model.model.*;
import play.BuiltInComponents;
import play.Mode;
import play.routing.Router;
import play.routing.RoutingDsl;
import play.server.Server;

import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

import static play.mvc.Results.*;

/**
 * A rest server for using an
 * {@link TransformProcess} based on simple
 * csv values and a schema via REST.
 * <p>
 * The input values are an {@link SingleCSVRecord}
 * which (based on the input schema) will automatically
 * have their values transformed.
 *
 * @author Adam Gibson
 */
@Slf4j
@Data
public class CSVSparkTransformServer extends SparkTransformServer {
    private CSVSparkTransform transform;

    public void runMain(String[] args) throws Exception {
        JCommander jcmdr = new JCommander(this);

        try {
            jcmdr.parse(args);
        } catch (ParameterException e) {
            //User provides invalid input -> print the usage info
            jcmdr.usage();
            if (jsonPath == null)
                System.err.println("Json path parameter is missing.");
            try {
                Thread.sleep(500);
            } catch (Exception e2) {
            }
            System.exit(1);
        }

        if (jsonPath != null) {
            String json = FileUtils.readFileToString(new File(jsonPath));
            TransformProcess transformProcess = TransformProcess.fromJson(json);
            transform = new CSVSparkTransform(transformProcess);
        } else {
            log.warn("Server started with no json for transform process. Please ensure you specify a transform process via sending a post request with raw json"
                    + "to /transformprocess");
        }

        //Set play secret key, if required
        //http://www.playframework.com/documentation/latest/ApplicationSecret
        String crypto = System.getProperty("play.crypto.secret");
        if (crypto == null || "changeme".equals(crypto) || "".equals(crypto) ) {
            byte[] newCrypto = new byte[1024];

            new Random().nextBytes(newCrypto);

            String base64 = Base64.getEncoder().encodeToString(newCrypto);
            System.setProperty("play.crypto.secret", base64);
        }


        server = Server.forRouter(Mode.PROD, port, this::createRouter);
    }

    protected Router createRouter(BuiltInComponents b){
        RoutingDsl routingDsl = RoutingDsl.fromComponents(b);

        routingDsl.GET("/transformprocess").routingTo(req -> {
            try {
                if (transform == null)
                    return badRequest();
                return ok(transform.getTransformProcess().toJson()).as(contentType);
            } catch (Exception e) {
                log.error("Error in GET /transformprocess",e);
                return internalServerError(e.getMessage());
            }
        });

        routingDsl.POST("/transformprocess").routingTo(req -> {
            try {
                TransformProcess transformProcess = TransformProcess.fromJson(getJsonText(req));
                setCSVTransformProcess(transformProcess);
                log.info("Transform process initialized");
                return ok(objectMapper.writeValueAsString(transformProcess)).as(contentType);
            } catch (Exception e) {
                log.error("Error in POST /transformprocess",e);
                return internalServerError(e.getMessage());
            }
        });

        routingDsl.POST("/transformincremental").routingTo(req -> {
            if (isSequence(req)) {
                try {
                    BatchCSVRecord record = objectMapper.readValue(getJsonText(req), BatchCSVRecord.class);
                    if (record == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformSequenceIncremental(record))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformincremental", e);
                    return internalServerError(e.getMessage());
                }
            } else {
                try {
                    SingleCSVRecord record = objectMapper.readValue(getJsonText(req), SingleCSVRecord.class);
                    if (record == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformIncremental(record))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformincremental", e);
                    return internalServerError(e.getMessage());
                }
            }
        });

        routingDsl.POST("/transform").routingTo(req -> {
            if (isSequence(req)) {
                try {
                    SequenceBatchCSVRecord batch = transformSequence(objectMapper.readValue(getJsonText(req), SequenceBatchCSVRecord.class));
                    if (batch == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(batch)).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transform", e);
                    return internalServerError(e.getMessage());
                }
            } else {
                try {
                    BatchCSVRecord input = objectMapper.readValue(getJsonText(req), BatchCSVRecord.class);
                    BatchCSVRecord batch = transform(input);
                    if (batch == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(batch)).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transform", e);
                    return internalServerError(e.getMessage());
                }
            }
        });

        routingDsl.POST("/transformincrementalarray").routingTo(req -> {
            if (isSequence(req)) {
                try {
                    BatchCSVRecord record = objectMapper.readValue(getJsonText(req), BatchCSVRecord.class);
                    if (record == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformSequenceArrayIncremental(record))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformincrementalarray", e);
                    return internalServerError(e.getMessage());
                }
            } else {
                try {
                    SingleCSVRecord record = objectMapper.readValue(getJsonText(req), SingleCSVRecord.class);
                    if (record == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformArrayIncremental(record))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformincrementalarray", e);
                    return internalServerError(e.getMessage());
                }
            }
        });

        routingDsl.POST("/transformarray").routingTo(req -> {
            if (isSequence(req)) {
                try {
                    SequenceBatchCSVRecord batchCSVRecord = objectMapper.readValue(getJsonText(req), SequenceBatchCSVRecord.class);
                    if (batchCSVRecord == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformSequenceArray(batchCSVRecord))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformarray", e);
                    return internalServerError(e.getMessage());
                }
            } else {
                try {
                    BatchCSVRecord batchCSVRecord = objectMapper.readValue(getJsonText(req), BatchCSVRecord.class);
                    if (batchCSVRecord == null)
                        return badRequest();
                    return ok(objectMapper.writeValueAsString(transformArray(batchCSVRecord))).as(contentType);
                } catch (Exception e) {
                    log.error("Error in /transformarray", e);
                    return internalServerError(e.getMessage());
                }
            }
        });

        return routingDsl.build();
    }

    public static void main(String[] args) throws Exception {
        new CSVSparkTransformServer().runMain(args);
    }

    /**
     * @param transformProcess
     */
    @Override
    public void setCSVTransformProcess(TransformProcess transformProcess) {
        this.transform = new CSVSparkTransform(transformProcess);
    }

    @Override
    public void setImageTransformProcess(ImageTransformProcess imageTransformProcess) {
        log.error("Unsupported operation: setImageTransformProcess not supported for class", getClass());
        throw new UnsupportedOperationException("Invalid operation for " + this.getClass());
    }

    /**
     * @return
     */
    @Override
    public TransformProcess getCSVTransformProcess() {
        return transform.getTransformProcess();
    }

    @Override
    public ImageTransformProcess getImageTransformProcess() {
        log.error("Unsupported operation: getImageTransformProcess not supported for class", getClass());
        throw new UnsupportedOperationException("Invalid operation for " + this.getClass());
    }


    /**
     *
     */
    /**
     * @param transform
     * @return
     */
    @Override
    public SequenceBatchCSVRecord transformSequenceIncremental(BatchCSVRecord transform) {
        return this.transform.transformSequenceIncremental(transform);
    }

    /**
     * @param batchCSVRecord
     * @return
     */
    @Override
    public SequenceBatchCSVRecord transformSequence(SequenceBatchCSVRecord batchCSVRecord) {
        return transform.transformSequence(batchCSVRecord);
    }

    /**
     * @param batchCSVRecord
     * @return
     */
    @Override
    public Base64NDArrayBody transformSequenceArray(SequenceBatchCSVRecord batchCSVRecord) {
        return this.transform.transformSequenceArray(batchCSVRecord);
    }

    /**
     * @param singleCsvRecord
     * @return
     */
    @Override
    public Base64NDArrayBody transformSequenceArrayIncremental(BatchCSVRecord singleCsvRecord) {
        return this.transform.transformSequenceArrayIncremental(singleCsvRecord);
    }

    /**
     * @param transform
     * @return
     */
    @Override
    public SingleCSVRecord transformIncremental(SingleCSVRecord transform) {
        return this.transform.transform(transform);
    }

    @Override
    public SequenceBatchCSVRecord transform(SequenceBatchCSVRecord batchCSVRecord) {
        return this.transform.transform(batchCSVRecord);
    }

    /**
     * @param batchCSVRecord
     * @return
     */
    @Override
    public BatchCSVRecord transform(BatchCSVRecord batchCSVRecord) {
        return transform.transform(batchCSVRecord);
    }

    /**
     * @param batchCSVRecord
     * @return
     */
    @Override
    public Base64NDArrayBody transformArray(BatchCSVRecord batchCSVRecord) {
        try {
            return this.transform.toArray(batchCSVRecord);
        } catch (IOException e) {
            log.error("Error in transformArray",e);
            throw new IllegalStateException("Transform array shouldn't throw exception");
        }
    }

    /**
     * @param singleCsvRecord
     * @return
     */
    @Override
    public Base64NDArrayBody transformArrayIncremental(SingleCSVRecord singleCsvRecord) {
        try {
            return this.transform.toArray(singleCsvRecord);
        } catch (IOException e) {
            log.error("Error in transformArrayIncremental",e);
            throw new IllegalStateException("Transform array shouldn't throw exception");
        }
    }

    @Override
    public Base64NDArrayBody transformIncrementalArray(SingleImageRecord singleImageRecord) throws IOException {
        log.error("Unsupported operation: transformIncrementalArray(SingleImageRecord) not supported for class", getClass());
        throw new UnsupportedOperationException("Invalid operation for " + this.getClass());
    }

    @Override
    public Base64NDArrayBody transformArray(BatchImageRecord batchImageRecord) throws IOException {
        log.error("Unsupported operation: transformArray(BatchImageRecord) not supported for class", getClass());
        throw new UnsupportedOperationException("Invalid operation for " + this.getClass());
    }
}
