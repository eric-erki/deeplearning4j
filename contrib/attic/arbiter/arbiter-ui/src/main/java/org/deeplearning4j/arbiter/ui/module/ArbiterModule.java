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

package org.deeplearning4j.arbiter.ui.module;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.deeplearning4j.core.storage.Persistable;
import org.deeplearning4j.core.storage.StatsStorage;
import org.deeplearning4j.core.storage.StatsStorageEvent;
import org.deeplearning4j.core.storage.StatsStorageListener;
import org.deeplearning4j.arbiter.BaseNetworkSpace;
import org.deeplearning4j.arbiter.layers.LayerSpace;
import org.deeplearning4j.arbiter.optimize.api.ParameterSpace;
import org.deeplearning4j.arbiter.optimize.api.termination.TerminationCondition;
import org.deeplearning4j.arbiter.optimize.config.OptimizationConfiguration;
import org.deeplearning4j.arbiter.optimize.runner.CandidateStatus;
import org.deeplearning4j.arbiter.ui.UpdateStatus;
import org.deeplearning4j.arbiter.ui.data.GlobalConfigPersistable;
import org.deeplearning4j.arbiter.ui.data.ModelInfoPersistable;
import org.deeplearning4j.arbiter.ui.misc.UIUtils;
import org.deeplearning4j.arbiter.util.ObjectUtils;
import org.deeplearning4j.nn.conf.serde.JsonMappers;
import org.deeplearning4j.ui.VertxUIServer;
import org.deeplearning4j.ui.api.Component;
import org.deeplearning4j.ui.api.*;
import org.deeplearning4j.ui.components.chart.ChartLine;
import org.deeplearning4j.ui.components.chart.ChartScatter;
import org.deeplearning4j.ui.components.chart.style.StyleChart;
import org.deeplearning4j.ui.components.component.ComponentDiv;
import org.deeplearning4j.ui.components.component.style.StyleDiv;
import org.deeplearning4j.ui.components.table.ComponentTable;
import org.deeplearning4j.ui.components.table.style.StyleTable;
import org.deeplearning4j.ui.components.text.ComponentText;
import org.deeplearning4j.ui.components.text.style.StyleText;
import org.deeplearning4j.ui.i18n.I18NResource;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.nd4j.common.function.Function;
import org.nd4j.common.primitives.Pair;
import org.nd4j.shade.jackson.core.JsonProcessingException;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A Deeplearning4j {@link UIModule}, for integration with DL4J's user interface
 *
 * @author Alex Black
 */
@Slf4j
public class ArbiterModule implements UIModule {

    private static final DecimalFormat DECIMAL_FORMAT_2DP = new DecimalFormat("#.00");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm ZZ");
    public static final String ARBITER_UI_TYPE_ID = "ArbiterUI";

    private AtomicBoolean loggedArbiterAddress = new AtomicBoolean(false);
    private Map<String, StatsStorage> knownSessionIDs = Collections.synchronizedMap(new LinkedHashMap<>());
    private String currentSessionID;

    private Map<String, Long> lastUpdateForSession = Collections.synchronizedMap(new HashMap<>());

    //Styles for UI:
    private static final StyleTable STYLE_TABLE = new StyleTable.Builder()
            .width(100, LengthUnit.Percent)
            .backgroundColor(Color.WHITE)
            .borderWidth(1)
            .columnWidths(LengthUnit.Percent, 30, 70)
            .build();

    private static final StyleTable STYLE_TABLE3_25_25_50 = new StyleTable.Builder()
            .width(100, LengthUnit.Percent)
            .backgroundColor(Color.WHITE)
            .borderWidth(1)
            .columnWidths(LengthUnit.Percent, 25, 25, 50)
            .build();

    private static final StyleDiv STYLE_DIV_WIDTH_100_PC = new StyleDiv.Builder()
            .width(100, LengthUnit.Percent)
            .build();

    private static final ComponentDiv DIV_SPACER_20PX = new ComponentDiv(new StyleDiv.Builder()
            .width(100,LengthUnit.Percent)
            .height(20, LengthUnit.Px).build());

    private static final ComponentDiv DIV_SPACER_60PX = new ComponentDiv(new StyleDiv.Builder()
            .width(100,LengthUnit.Percent)
            .height(60, LengthUnit.Px).build());

    private static final StyleChart STYLE_CHART_560_320 = new StyleChart.Builder()
            .width(560, LengthUnit.Px)
            .height(320, LengthUnit.Px)
            .build();

    private static final StyleChart STYLE_CHART_800_400 = new StyleChart.Builder()
            .width(800, LengthUnit.Px)
            .height(400, LengthUnit.Px)
            .build();


    private StyleText STYLE_TEXT_SZ12 = new StyleText.Builder()
            .fontSize(12)
            .build();

    //Set whitespacePre(true) to avoid losing new lines, tabs, multiple spaces etc
    private StyleText STYLE_TEXT_SZ10_WHITESPACE_PRE = new StyleText.Builder()
            .fontSize(10)
            .whitespacePre(true)
            .build();


    @Override
    public List<String> getCallbackTypeIDs() {
        return Collections.singletonList(ARBITER_UI_TYPE_ID);
    }

    @Override
    public List<Route> getRoutes() {
        boolean multiSession = VertxUIServer.getMultiSession().get();
        List<Route> r = new ArrayList<>();
        r.add(new Route("/arbiter/multisession", HttpMethod.GET,
                (path, rc) -> rc.response().end(multiSession ? "true" : "false")));
        if (multiSession) {
            r.add(new Route("/arbiter", HttpMethod.GET, (path, rc) -> this.listSessions(rc)));
            r.add(new Route("/arbiter/:sessionId", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    rc.response()
                            .putHeader("content-type", "text/html; charset=utf-8")
                            .sendFile("templates/ArbiterUI.html");
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));

            r.add(new Route("/arbiter/:sessionId/lastUpdate", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    this.getLastUpdateTime(path.get(0), rc);
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));
            r.add(new Route("/arbiter/:sessionId/candidateInfo/:id", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    this.getCandidateInfo(path.get(0), path.get(1), rc);
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));
            r.add(new Route("/arbiter/:sessionId/config", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    this.getOptimizationConfig(path.get(0), rc);
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));
            r.add(new Route("/arbiter/:sessionId/results", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    this.getSummaryResults(path.get(0), rc);
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));
            r.add(new Route("/arbiter/:sessionId/summary", HttpMethod.GET, (path, rc) -> {
                if (knownSessionIDs.containsKey(path.get(0))) {
                    this.getSummaryStatus(path.get(0), rc);
                } else {
                    sessionNotFound(path.get(0), rc.request().path(), rc);
                }
            }));
        } else {
            r.add(new Route("/arbiter", HttpMethod.GET, (path, rc) -> rc.response()
                    .putHeader("content-type", "text/html; charset=utf-8")
                    .sendFile("templates/ArbiterUI.html")));
            r.add(new Route("/arbiter/lastUpdate", HttpMethod.GET, (path, rc) -> this.getLastUpdateTime(null, rc)));
            r.add(new Route("/arbiter/candidateInfo/:id", HttpMethod.GET,
                    (path, rc) -> this.getCandidateInfo(null, path.get(0), rc)));
            r.add(new Route("/arbiter/config", HttpMethod.GET, (path, rc) -> this.getOptimizationConfig(null, rc)));
            r.add(new Route("/arbiter/results", HttpMethod.GET, (path, rc) -> this.getSummaryResults(null, rc)));
            r.add(new Route("/arbiter/summary", HttpMethod.GET, (path, rc) -> this.getSummaryStatus(null, rc)));

            r.add(new Route("/arbiter/sessions/current", HttpMethod.GET, (path, rc) -> this.currentSession(rc)));
            r.add(new Route("/arbiter/sessions/set/:to", HttpMethod.GET,
                    (path, rc) -> this.setSession(path.get(0), rc)));
        }
        // common for single- and multi-session mode
        r.add(new Route("/arbiter/sessions/all", HttpMethod.GET, (path, rc) -> this.sessionInfo(rc)));

        return r;
    }


    /**
     * Load StatsStorage via provider, or return "not found"
     *
     * @param sessionId  session ID to look fo with provider
     * @param targetPath one of overview / model / system, or null
     * @param rc routing context
     */
    private void sessionNotFound(String sessionId, String targetPath, RoutingContext rc) {
        Function<String, Boolean> loader = VertxUIServer.getInstance().getStatsStorageLoader();
        if (loader != null && loader.apply(sessionId)) {
            if (targetPath != null) {
                rc.reroute(targetPath);
            } else {
                rc.response().end();
            }
        } else {
            rc.response().setStatusCode(HttpResponseStatus.NOT_FOUND.code())
                    .end("Unknown session ID: " + sessionId);
        }
    }


    /**
     * List optimization sessions. Returns a HTML list of arbiter sessions
     */
    private synchronized void listSessions(RoutingContext rc) {
        StringBuilder sb = new StringBuilder("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "        <meta charset=\"utf-8\">\n" +
                "        <title>Optimization sessions - DL4J Arbiter UI</title>\n" +
                "    </head>\n" +
                "\n" +
                "    <body>\n" +
                "        <h1>DL4J Arbiter UI</h1>\n" +
                "        <p>UI server is in multi-session mode." +
                " To visualize an optimization session, please select one from the following list.</p>\n" +
                "        <h2>List of attached optimization sessions</h2>\n");
        if (!knownSessionIDs.isEmpty()) {
            sb.append("        <ul>");
            for (String sessionId : knownSessionIDs.keySet()) {
                sb.append("            <li><a href=\"/arbiter/")
                        .append(sessionId).append("\">")
                        .append(sessionId).append("</a></li>\n");
            }
            sb.append("        </ul>");
        } else {
            sb.append("No optimization session attached.");
        }

        sb.append("    </body>\n" +
                "</html>\n");

        rc.response()
                .putHeader("content-type", "text/html; charset=utf-8")
                .end(sb.toString());
    }

    @Override
    public void reportStorageEvents(Collection<StatsStorageEvent> events) {
        boolean attachedArbiter = false;
        for (StatsStorageEvent sse : events) {
            if (ARBITER_UI_TYPE_ID.equals(sse.getTypeID())) {
                if (sse.getEventType() == StatsStorageListener.EventType.PostStaticInfo) {
                    knownSessionIDs.put(sse.getSessionID(), sse.getStatsStorage());
                }

                Long lastUpdate = lastUpdateForSession.get(sse.getSessionID());
                if (lastUpdate == null) {
                    lastUpdateForSession.put(sse.getSessionID(), sse.getTimestamp());
                } else if (sse.getTimestamp() > lastUpdate) {
                    lastUpdateForSession.put(sse.getSessionID(), sse.getTimestamp()); //Should be thread safe - read only elsewhere
                }
                attachedArbiter = true;
            }
        }

        if(currentSessionID == null){
            getDefaultSession();
        }

        if(attachedArbiter && !loggedArbiterAddress.getAndSet(true)){
            String address = UIServer.getInstance().getAddress();
            address += "/arbiter";
            log.info("DL4J Arbiter Hyperparameter Optimization UI: {}", address);
        }
    }

    @Override
    public synchronized void onAttach(StatsStorage statsStorage) {
        for (String sessionID : statsStorage.listSessionIDs()) {
            for (String typeID : statsStorage.listTypeIDsForSession(sessionID)) {
                if (!ARBITER_UI_TYPE_ID.equals(typeID))
                    continue;
                knownSessionIDs.put(sessionID, statsStorage);
            }
        }

        if (currentSessionID == null)
            getDefaultSession();
    }

    private void currentSession(RoutingContext rc) {
        String sid = currentSessionID == null ? "" : currentSessionID;
        rc.response()
                .putHeader("content-type", "application/json")
                .end(asJson(sid));
    }

    private void sessionInfo(RoutingContext rc) {
        rc.response()
                .putHeader("content-type", "application/json")
                .end(asJson(knownSessionIDs.keySet()));
    }

    private void setSession(String newSessionID, RoutingContext rc) {
        log.debug("Arbiter UI: Set to session {}", newSessionID);

        if (knownSessionIDs.containsKey(newSessionID)) {
            currentSessionID = newSessionID;
            rc.response().end();
        } else {
            rc.response().setStatusCode(HttpResponseStatus.BAD_REQUEST.code()).end("Unknown session ID: " + newSessionID);
        }
    }

    private void getDefaultSession() {
        if (currentSessionID != null)
            return;

        long mostRecentTime = Long.MIN_VALUE;
        String sessionID = null;
        for (Map.Entry<String, StatsStorage> entry : knownSessionIDs.entrySet()) {
            List<Persistable> staticInfos = entry.getValue().getAllStaticInfos(entry.getKey(), ARBITER_UI_TYPE_ID);
            if (staticInfos == null || staticInfos.size() == 0)
                continue;
            Persistable p = staticInfos.get(0);
            long thisTime = p.getTimeStamp();
            if (thisTime > mostRecentTime) {
                mostRecentTime = thisTime;
                sessionID = entry.getKey();
            }
        }

        if (sessionID != null) {
            currentSessionID = sessionID;
        }
    }

    @Override
    public void onDetach(StatsStorage statsStorage) {
        for (String s : knownSessionIDs.keySet()) {
            if (knownSessionIDs.get(s) == statsStorage) {
                knownSessionIDs.remove(s);
            }
        }
    }

    @Override
    public List<I18NResource> getInternationalizationResources() {
        return Collections.emptyList();
    }

    /**
     * Return the last update time for the page
     * @param sessionId session ID (optional, for multi-session mode)
     * @param rc routing context
     */
    private void getLastUpdateTime(String sessionId, RoutingContext rc){
        if (sessionId == null) {
            sessionId = currentSessionID;
        }
        StatsStorage ss = knownSessionIDs.get(sessionId);
        List<Persistable> latestUpdates = ss.getLatestUpdateAllWorkers(sessionId, ARBITER_UI_TYPE_ID);
        long t = 0;
        if (latestUpdates.isEmpty()) {
            t = System.currentTimeMillis();
        } else {
            for (Persistable update : latestUpdates) {
                if (update.getTimeStamp() > t) {
                    t = update.getTimeStamp();
                }
            }
        }
        UpdateStatus us = new UpdateStatus(t, t, t);

        rc.response().putHeader("content-type", "application/json").end(asJson(us));
    }

    private String asJson(Object o){
        try{
            return JsonMappers.getMapper().writeValueAsString(o);
        } catch (JsonProcessingException e){
            throw new RuntimeException("Error converting object to JSON", e);
        }
    }

    /**
     * Get the info for a specific candidate - last section in the UI
     * @param sessionId session ID (optional, for multi-session mode)
     * @param candidateId ID for the candidate
     * @param rc routing context
     */
    private void getCandidateInfo(String sessionId, String candidateId, RoutingContext rc){
        if (sessionId == null) {
            sessionId = currentSessionID;
        }
        StatsStorage ss = knownSessionIDs.get(sessionId);
        if(ss == null){
            log.debug("getModelLastUpdateTimes(): Session ID is unknown: {}", sessionId);
            rc.response().end();
            return;
        }

        GlobalConfigPersistable gcp = (GlobalConfigPersistable)ss
                .getStaticInfo(sessionId, ARBITER_UI_TYPE_ID, GlobalConfigPersistable.GLOBAL_WORKER_ID);
        OptimizationConfiguration oc = gcp.getOptimizationConfiguration();

        Persistable p = ss.getLatestUpdate(sessionId, ARBITER_UI_TYPE_ID, candidateId);
        if(p == null){
            String title = "No results found for model " + candidateId + ".";
            ComponentText ct = new ComponentText.Builder(title,STYLE_TEXT_SZ12).build();
            rc.response()
                    .putHeader("content-type", "application/json")
                    .end(asJson(ct));
            return;
        }

        ModelInfoPersistable mip = (ModelInfoPersistable)p;

        //First: static info
        // Hyperparameter configuration/settings
        // Number of parameters
        // Maybe memory info in the future?

        //Second: dynamic info
        //Runtime
        // Performance stats (total minibatches, total time,
        // Score vs. time

        List<Component> components = new ArrayList<>();

        //First table: mix of static + dynamic in a table
        long runtimeDurationMs = mip.getLastUpdateTime() - mip.getTimeStamp();
        double avgMinibatchesPerSec = mip.getTotalNumUpdates() / (runtimeDurationMs/1000.0);
        String avgMinibatchesPerSecStr = DECIMAL_FORMAT_2DP.format(avgMinibatchesPerSec);
        String runtimeStr = UIUtils.formatDuration(runtimeDurationMs);

        if(mip.getStatus() == CandidateStatus.Failed){
            runtimeStr = "";
            avgMinibatchesPerSecStr = "";
        }

        String[][] table = new String[][]{
                {"Model Index", String.valueOf(mip.getModelIdx())},
                {"Status", mip.getStatus().toString()},
                {"Model Score", mip.getScore() == null ? "" : String.valueOf(mip.getScore())},
                {"Created", TIME_FORMATTER.print(mip.getTimeStamp())},
                {"Runtime", runtimeStr},
                {"Total Number of Model Updates", String.valueOf(mip.getTotalNumUpdates())},
                {"Average # Updates / Sec", avgMinibatchesPerSecStr},
                {"Number of Parameters", String.valueOf(mip.getNumParameters())},
                {"Number of Layers", String.valueOf(mip.getNumLayers())}
        };

        ComponentTable cTable = new ComponentTable.Builder(STYLE_TABLE)
                .content(table)
                .header("Model Information", "")
                .build();
        components.add(cTable);


        //Second: parameter space values, in multiple tables
        double[] paramSpaceValues = mip.getParamSpaceValues();
        if(paramSpaceValues != null){
            BaseNetworkSpace bns = (BaseNetworkSpace)oc.getCandidateGenerator().getParameterSpace();
            Map<String,ParameterSpace> m = bns.getNestedSpaces();

            String[][] hSpaceTable = new String[m.size()][3];
            int i=0;
            for(Map.Entry<String,ParameterSpace> e : m.entrySet()){
                hSpaceTable[i][0] = e.getKey();
                Object currCandidateValue = e.getValue().getValue(paramSpaceValues);
                hSpaceTable[i][1] = ObjectUtils.valueToString(currCandidateValue);
                hSpaceTable[i][2] = e.getValue().toString();
                i++;
            }

            String[] hSpaceTableHeader = new String[]{"Hyperparameter", "Model Value", "Hyperparameter Space"};

            ComponentTable ct2 = new ComponentTable.Builder(STYLE_TABLE3_25_25_50)
                    .content(hSpaceTable)
                    .header(hSpaceTableHeader)
                    .build();


            String title = "Global Network Configuration";
            components.add(DIV_SPACER_20PX);
            components.add(new ComponentText.Builder(title, STYLE_TEXT_SZ12).build());
            components.add(ct2);

            List<BaseNetworkSpace.LayerConf> layerConfs = bns.getLayerSpaces();

            for(BaseNetworkSpace.LayerConf l : layerConfs){
                LayerSpace<?> ls = l.getLayerSpace();
                Map<String,ParameterSpace> lpsm = ls.getNestedSpaces();

                String[][] t = new String[lpsm.size()][3];
                i=0;
                for(Map.Entry<String,ParameterSpace> e : lpsm.entrySet()){
                    t[i][0] = e.getKey();
                    Object currCandidateValue = e.getValue().getValue(paramSpaceValues);
                    t[i][1] = ObjectUtils.valueToString(currCandidateValue);
                    t[i][2] = e.getValue().toString();
                    i++;
                }

                ComponentTable ct3 = new ComponentTable.Builder(STYLE_TABLE3_25_25_50)
                        .content(t)
                        .header(hSpaceTableHeader)
                        .build();

                title = "Layer Space: " + ls.getClass().getSimpleName() + ", Name: " + l.getLayerName();

                components.add(DIV_SPACER_20PX);
                components.add(new ComponentText.Builder(title, STYLE_TEXT_SZ12).build());
                components.add(ct3);
            }
        }


        //Third: Score vs. time chart
        int[] iters = mip.getIter();
        float[] scores = mip.getScoreVsIter();

        if(iters != null) {
            double[] si = new double[iters.length];
            double[] scoresD = new double[iters.length];

            double minScore = Double.MAX_VALUE;
            double maxScore = -Double.MAX_VALUE;
            for( int i=0; i<iters.length; i++ ){
                si[i] = iters[i];
                scoresD[i] = scores[i];
                minScore = Math.min(minScore, scoresD[i]);
                maxScore = Math.max(maxScore, scoresD[i]);
            }

            double[] chartMinMax = UIUtils.graphNiceRange(maxScore, minScore, 5);

            ChartLine cl = new ChartLine.Builder("Model Score vs. Iteration", STYLE_CHART_800_400)
                    .addSeries("Score", si, scoresD )
                    .setYMin(chartMinMax[0])
                    .setYMax(chartMinMax[1])
                    .build();

            components.add(DIV_SPACER_60PX);
            components.add(cl);
        }


        //Post full network configuration JSON, if available:
        String modelJson = mip.getModelConfigJson();
        if(modelJson != null){
            components.add(DIV_SPACER_60PX);
            components.add(new ComponentDiv(STYLE_DIV_WIDTH_100_PC, new ComponentText("Model Configuration", STYLE_TEXT_SZ12)));
            ComponentText jsonText = new ComponentText(modelJson, STYLE_TEXT_SZ10_WHITESPACE_PRE);
            ComponentDiv cd = new ComponentDiv(STYLE_DIV_WIDTH_100_PC, jsonText);
            components.add(cd);
        }


        //Post exception stack trace, if necessary:
        if( mip.getExceptionStackTrace() != null ){
            components.add(DIV_SPACER_60PX);
            components.add(new ComponentDiv(STYLE_DIV_WIDTH_100_PC, new ComponentText("Model Exception - Stack Trace", STYLE_TEXT_SZ12)));
            ComponentText exText = new ComponentText(mip.getExceptionStackTrace(), STYLE_TEXT_SZ10_WHITESPACE_PRE);
            ComponentDiv cd = new ComponentDiv(STYLE_DIV_WIDTH_100_PC, exText);
            components.add(cd);
        }

        ComponentDiv cd = new ComponentDiv(STYLE_DIV_WIDTH_100_PC, components);

        rc.response().putHeader("content-type", "application/json").end(asJson(cd));
    }

    /**
     * Get the optimization configuration - second section in the page
     * @param sessionId session ID (optional, for multi-session mode)
     * @param rc routing context
     */
    private void getOptimizationConfig(String sessionId, RoutingContext rc){
        if (sessionId == null) {
            sessionId = currentSessionID;
        }
        StatsStorage ss = knownSessionIDs.get(sessionId);
        if(ss == null){
            log.debug("getOptimizationConfig(): Session ID is unknown: {}", sessionId);
            rc.response().end();
            return;
        }

        Persistable p = ss.getStaticInfo(sessionId, ARBITER_UI_TYPE_ID, GlobalConfigPersistable.GLOBAL_WORKER_ID);

        if(p == null){
            log.debug("No static info");
            rc.response().end();
            return;
        }

        List<Component> components = new ArrayList<>();

        GlobalConfigPersistable gcp = (GlobalConfigPersistable)p;
        OptimizationConfiguration oc = gcp.getOptimizationConfiguration();

        //Report optimization settings/configuration.
        String[] tableHeader = {"Configuration", "Value"};
        String [] dataSourceOrProvider;
        if (oc.getDataProvider() != null) {
            dataSourceOrProvider = new String[] {"Data Provider", oc.getDataProvider().toString()};
        }
        else {
            dataSourceOrProvider = new String[] {"Data Source", oc.getDataSource().getCanonicalName()};
        }
        String[][] table = new String[][]{
                {"Candidate Generator", oc.getCandidateGenerator().getClass().getSimpleName()},
                dataSourceOrProvider,
                {"Score Function", oc.getScoreFunction().toString()},
                {"Result Saver", oc.getResultSaver().toString()},
        };

        ComponentTable ct = new ComponentTable.Builder(STYLE_TABLE)
                .content(table)
                .header(tableHeader)
                .build();
        components.add(ct);


        String title = "Global Network Configuration";
        components.add(DIV_SPACER_20PX);
        components.add(new ComponentText.Builder(title, STYLE_TEXT_SZ12).build());
        BaseNetworkSpace<?> ps = (BaseNetworkSpace)oc.getCandidateGenerator().getParameterSpace();
        Map<String,ParameterSpace> m = ps.getNestedSpaces();

        String[][] hSpaceTable = new String[m.size()][2];
        int i=0;
        for(Map.Entry<String,ParameterSpace> e : m.entrySet()){
            hSpaceTable[i][0] = e.getKey();
            hSpaceTable[i][1] = e.getValue().toString();
            i++;
        }

        components.add(DIV_SPACER_20PX);
        String[] hSpaceTableHeader = new String[]{"Hyperparameter", "Hyperparameter Configuration"};

        ComponentTable ct2 = new ComponentTable.Builder(STYLE_TABLE)
                .content(hSpaceTable)
                .header(hSpaceTableHeader)
                .build();
        components.add(ct2);

        //Configuration for each layer:
        List<BaseNetworkSpace.LayerConf> layerConfs = ps.getLayerSpaces();
        for(BaseNetworkSpace.LayerConf l : layerConfs){
            LayerSpace<?> ls = l.getLayerSpace();
            Map<String,ParameterSpace> lpsm = ls.getNestedSpaces();

            String[][] t = new String[lpsm.size()][2];
            i=0;
            for(Map.Entry<String,ParameterSpace> e : lpsm.entrySet()){
                t[i][0] = e.getKey();
                t[i][1] = e.getValue().toString();
                i++;
            }

            ComponentTable ct3 = new ComponentTable.Builder(STYLE_TABLE)
                    .content(t)
                    .header(hSpaceTableHeader)
                    .build();

            title = "Layer Space: " + ls.getClass().getSimpleName() + ", Name: " + l.getLayerName();

            components.add(DIV_SPACER_20PX);
            components.add(new ComponentText.Builder(title, STYLE_TEXT_SZ12).build());
            components.add(ct3);
        }

        ComponentDiv cd = new ComponentDiv(STYLE_DIV_WIDTH_100_PC, components);

        rc.response().putHeader("content-type", "application/json").end(asJson(cd));
    }

    /**
     * Get candidates summary results list - third section on the page: Results table
     * @param sessionId session ID (optional, for multi-session mode)
     * @param rc routing context
     */
    private void getSummaryResults(String sessionId, RoutingContext rc){
        if (sessionId == null) {
            sessionId = currentSessionID;
        }
        StatsStorage ss = knownSessionIDs.get(sessionId);
        if(ss == null){
            log.debug("getSummaryResults(): Session ID is unknown: {}", sessionId);
            rc.response().end();
            return;
        }

        List<Persistable> allModelInfoTemp = new ArrayList<>(ss.getLatestUpdateAllWorkers(sessionId, ARBITER_UI_TYPE_ID));
        List<String[]> table = new ArrayList<>();
        for(Persistable per : allModelInfoTemp){
            ModelInfoPersistable mip = (ModelInfoPersistable)per;
            String score = (mip.getScore() == null ? "" : mip.getScore().toString());
            table.add(new String[]{mip.getModelIdx().toString(), score, mip.getStatus().toString()});
        }

        rc.response().putHeader("content-type", "application/json").end(asJson(table));
    }

    /**
     * Get summary status information: first section in the page
     * @param sessionId session ID (optional, for multi-session mode)
     * @param rc routing context
     */
    private void getSummaryStatus(String sessionId, RoutingContext rc){
        if (sessionId == null) {
            sessionId = currentSessionID;
        }
        StatsStorage ss = knownSessionIDs.get(sessionId);
        if(ss == null){
            log.debug("getOptimizationConfig(): Session ID is unknown: {}", sessionId);
            rc.response().end();
            return;
        }

        Persistable p = ss.getStaticInfo(sessionId, ARBITER_UI_TYPE_ID, GlobalConfigPersistable.GLOBAL_WORKER_ID);

        if(p == null){
            log.info("No static info");
            rc.response().end();
            return;
        }

        GlobalConfigPersistable gcp = (GlobalConfigPersistable)p;
        OptimizationConfiguration oc = gcp.getOptimizationConfiguration();
        long execStartTime = oc.getExecutionStartTime();



        //Charts:
        //Best model score vs. time
        //All candidate scores (scatter plot vs. time)

        //How to get this? query all model infos...

        List<Persistable> allModelInfoTemp = new ArrayList<>(ss.getLatestUpdateAllWorkers(sessionId, ARBITER_UI_TYPE_ID));
        List<ModelInfoPersistable> allModelInfo = new ArrayList<>();
        for(Persistable per : allModelInfoTemp){
            ModelInfoPersistable mip = (ModelInfoPersistable)per;
            if(mip.getStatus() == CandidateStatus.Complete && mip.getScore() != null && Double.isFinite(mip.getScore())){
                allModelInfo.add(mip);
            }
        }

        allModelInfo.sort(Comparator.comparingLong(Persistable::getTimeStamp));

        Pair<List<Component>, ModelInfoPersistable> chartsAndBest = getSummaryChartsAndBest(allModelInfo, oc.getScoreFunction().minimize(), execStartTime );

        //First: table - number completed, queued, running, failed, total
        //Best model index, score, and time
        //Total runtime
        //Termination conditions
        List<Component> components = new ArrayList<>();



        List<TerminationCondition> tcs = oc.getTerminationConditions();

        //TODO: I18N

        long bestTime;
        Double bestScore = null;
        String bestModelString = null;
        if(chartsAndBest.getSecond() != null){
            bestTime = chartsAndBest.getSecond().getTimeStamp();
            bestScore = chartsAndBest.getSecond().getScore();
            String sinceBest = UIUtils.formatDuration(System.currentTimeMillis() - bestTime);

            bestModelString = "Model " + chartsAndBest.getSecond().getModelIdx() + ", Found at " +
            TIME_FORMATTER.print(bestTime) + " (" + sinceBest + " ago)";
        }

        String execStartTimeStr = "";
        String execTotalRuntimeStr = "";
        if(execStartTime > 0){
            execStartTimeStr = TIME_FORMATTER.print(execStartTime);
            // allModelInfo is sorted by Persistable::getTimeStamp
            long lastCompleteTime = execStartTime;
            if (!allModelInfo.isEmpty()) {
                lastCompleteTime = allModelInfo.get(allModelInfo.size() - 1).getTimeStamp();
            }
            execTotalRuntimeStr = UIUtils.formatDuration(lastCompleteTime - execStartTime);
        }


        String[][] table = new String[][]{
                {"Models Completed", String.valueOf(gcp.getCandidatesCompleted())},
                {"Models Queued/Running", String.valueOf(gcp.getCandidatesQueued())},
                {"Models Failed", String.valueOf(gcp.getCandidatesFailed())},
                {"Models Total", String.valueOf(gcp.getCandidatesTotal())},
                {"Best Score", (bestScore != null ? String.valueOf(bestScore) : "")},
                {"Best Scoring Model", bestModelString != null ? bestModelString : ""},
                {"Optimization Runner", gcp.getOptimizationRunner()},
                {"Execution Start Time", execStartTimeStr},
                {"Total Runtime", execTotalRuntimeStr}
        };



        ComponentTable ct = new ComponentTable.Builder(STYLE_TABLE)
                .content(table)
                .header("Status", "")
                .build();

        components.add(ct);

        String[][] tcTable = new String[tcs.size()][2];
        for( int i=0; i<tcs.size(); i++ ){
            tcTable[i][0] = "Termination Condition " + i;
            tcTable[i][1] = tcs.get(i).toString();
        }

        components.add(DIV_SPACER_20PX);

        ComponentTable ct2 = new ComponentTable.Builder(STYLE_TABLE)
                .content(tcTable)
                .header("Termination Condition", "")
                .build();

        components.add(ct2);

        components.addAll(chartsAndBest.getFirst());


        ComponentDiv cd = new ComponentDiv(STYLE_DIV_WIDTH_100_PC, components);

        rc.response().putHeader("content-type", "application/json").end(asJson(cd));
    }


    private Pair<List<Component>,ModelInfoPersistable> getSummaryChartsAndBest(List<ModelInfoPersistable> allModelInfo,
                                                                               boolean minimize, long execStartTime){
        List<Double> bestX = new ArrayList<>();
        List<Double> bestY = new ArrayList<>();

        double[] allX = new double[allModelInfo.size()];
        double[] allY = new double[allModelInfo.size()];

        double bestScore = (minimize ? Double.MAX_VALUE : -Double.MAX_VALUE);
        double worstScore = (minimize ? -Double.MAX_VALUE : Double.MAX_VALUE);
        double lastTime = -1L;
        ModelInfoPersistable bestModel = null;
        for(int i=0; i<allModelInfo.size(); i++ ){
            ModelInfoPersistable mip = allModelInfo.get(i);
            double currScore = mip.getScore();
            double t = (mip.getTimeStamp() - execStartTime) / 60000.0;    //60000 ms per minute

            allX[i] = t;
            allY[i] = currScore;

            if(i == 0){
                bestX.add(t);
                bestY.add(currScore);
                bestScore = currScore;
                bestModel = mip;
            } else if((!minimize && currScore > bestScore) || (minimize && currScore < bestScore)){
                bestX.add(t);
                bestY.add(bestScore);
                bestX.add(t);  //TODO non-real time rendering support...
                bestY.add(currScore);

                bestScore = currScore;
                bestModel = mip;
            }

            if((!minimize && currScore < worstScore) || (minimize && currScore > worstScore)){
                worstScore = currScore;
            }

            if(t > lastTime){
                lastTime = t;
            }
        }


        double[] scatterGraphMinMax = UIUtils.graphNiceRange(Math.max(bestScore, worstScore), Math.min(bestScore, worstScore), 5);
        double[] lineGraphMinMax = UIUtils.graphNiceRange(
                bestY.stream().mapToDouble(s -> s).max().orElse(0),bestY.stream().mapToDouble(s -> s).min().orElse(0), 5
        );

        if(bestX.size() > 0) {
            bestX.add(lastTime);
            bestY.add(bestY.get(bestY.size() - 1));
        }


        double[] bestXd = new double[bestX.size()];
        double[] bestYd = new double[bestXd.length];
        for( int i=0; i<bestX.size(); i++ ){
            bestXd[i] = bestX.get(i);
            bestYd[i] = bestY.get(i);
        }

        List<Component> components = new ArrayList<>(2);

        ChartLine cl = new ChartLine.Builder("Best Model Score vs. Time (Minutes)", STYLE_CHART_560_320)
                .addSeries("Best Score vs. Time", bestXd, bestYd)
                .setYMin(lineGraphMinMax[0])
                .setYMax(lineGraphMinMax[1])
                .build();
        components.add(cl);

        ChartScatter cs = new ChartScatter.Builder("All Candidate Scores vs. Time (Minutes)", STYLE_CHART_560_320)
                .addSeries("Candidates", allX, allY)
                .setYMin(scatterGraphMinMax[0])
                .setYMax(scatterGraphMinMax[1])
                .build();

        components.add(cs);

        return new Pair<>(components, bestModel);
    }
}
