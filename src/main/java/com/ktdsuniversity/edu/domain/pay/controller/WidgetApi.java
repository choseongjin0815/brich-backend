package com.ktdsuniversity.edu.domain.pay.controller;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.ktdsuniversity.edu.domain.pay.service.PayService;
import com.ktdsuniversity.edu.domain.pay.vo.request.RequestPaymentVO;
import com.ktdsuniversity.edu.domain.user.vo.UserVO;
import com.ktdsuniversity.edu.global.common.CommonCodeVO;
import com.ktdsuniversity.edu.global.util.AuthenticationUtil;

import jakarta.servlet.http.HttpSession;

@Controller
public class WidgetApi {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${app.api.key}")
    String widgetSecretKey;

    @Autowired
    private PayService payService;

    // =========================
    // ✅ React용: 결제 진입 정보(JSON)
    // =========================

    @GetMapping(value = "/api/pay/blgr/{cdId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getBlgrPayInfo(@PathVariable String cdId) {
        UserVO loginUser = AuthenticationUtil.getUserVO();

        CommonCodeVO commonCodeVO = this.payService.payInfoService(cdId);

        Map<String, Object> res = new HashMap<>();
        res.put("amount", commonCodeVO.getValue1());
        res.put("cdNm", commonCodeVO.getCdNm());
        res.put("cdId", cdId);
        res.put("usrId", loginUser.getUsrId());
        res.put("cmpnId", null);

        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/api/pay/adv/{cmpnId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAdvPayInfo(@PathVariable String cmpnId) {
        UserVO loginUser = AuthenticationUtil.getUserVO();

        String amount = this.payService.payInfoServiceCampaignAmount(cmpnId);

        Map<String, Object> res = new HashMap<>();
        res.put("amount", amount);
        res.put("usrId", loginUser.getUsrId());
        res.put("cmpnId", cmpnId);
        res.put("cdId", null);
        res.put("cdNm", null);

        return ResponseEntity.ok(res);
    }

    // =========================
    // ✅ React/JSP 공용: prepay (orderId 기준 PKkey 저장)
    // =========================

    public static class PrepayRequest {
        public String orderId;
        public String orderName;
        public String price;
        public String cdId;
        public String usrId;   // 프론트에서 넘어와도 신뢰 X
        public String cmpnId;  // 캠페인 결제일 때만 사용
    }

    @PostMapping(value = "/orders/prepay", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Void> prepay(@RequestBody PrepayRequest req, HttpSession session) {
        UserVO loginUser = AuthenticationUtil.getUserVO();

        String safeUsrId = loginUser.getUsrId();
        String clientOrderId = req.orderId;
        String clientOrderName = req.orderName;
        String clientCdId = req.cdId;
        String clientPrice = String.valueOf(req.price);

        // subscription이면 usrId, 캠페인이면 cmpnId
        String clientId = isBlank(req.cmpnId) ? safeUsrId : req.cmpnId;

        logger.info("----- prepay ----- sessionId={}, orderId={}, orderName={}, cdId={}, price={}, usrId(server)={}, cmpnId={}, clientId={}",
                session.getId(), clientOrderId, clientOrderName, clientCdId, clientPrice, safeUsrId, req.cmpnId, clientId);

        RequestPaymentVO vo = new RequestPaymentVO();
        vo.setClientOrderId(clientOrderId);
        vo.setClientOrderName(clientOrderName);
        vo.setClientId(clientId);
        vo.setClientPrice(clientPrice);
        vo.setClientCdId(clientCdId);

        String pkKey = this.payService.beforePaymentInfoSave(vo);

        if ("1004".equals(loginUser.getAutr())) {
            logger.info("autr=1004, beforeCampaigninfo clientId={}", vo.getClientId());
            pkKey = this.payService.beforeCampaigninfo(vo.getClientId());
        }

        session.setAttribute("PREPAY_PKKEY_" + clientOrderId, pkKey);
        logger.info("[prepay] saved sessionKey={}, pkKey={}", "PREPAY_PKKEY_" + clientOrderId, pkKey);

        return ResponseEntity.ok().build();
    }

    // =========================
    // ✅ React/JSP 공용: confirm (세션에서 pkkey 꺼내 검증)
    // =========================

    public static class ConfirmRequest {
        public String paymentKey;
        public String orderId;
        public String amount;
    }

    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<JSONObject> confirmPayment(@RequestBody ConfirmRequest req /*, HttpSession session*/) throws Exception {
    	UserVO loginUser = AuthenticationUtil.getUserVO();
        logger.info("============= 결제 confirm 시작 =============");
        logger.info("[confirm] sessionId={}, orderId={}, amount={}, paymentKey={}", req.orderId, req.amount, req.paymentKey);

        String paymentKey = req.paymentKey;
        String orderId = req.orderId;
        String amount = req.amount;

        JSONObject obj = new JSONObject();
        obj.put("orderId", orderId);
        obj.put("amount", amount);
        obj.put("paymentKey", paymentKey);

        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        URL url = new URL("https://api.tosspayments.com/v1/payments/confirm");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestProperty("Authorization", authorizations);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(obj.toString().getBytes(StandardCharsets.UTF_8));
        }

        int code = connection.getResponseCode();
        boolean isSuccess = code == 200;

        InputStream responseStream = isSuccess ? connection.getInputStream() : connection.getErrorStream();

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try (Reader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8)) {
            jsonObject = (JSONObject) parser.parse(reader);
        }

        // ✅ toss confirm 성공일 때만 우리 DB 업데이트 로직 들어감
        if (code == 200) {
            String orderName = (String) jsonObject.get("orderName");

            JSONObject easyPay = (JSONObject) jsonObject.get("easyPay");
            String easyAmount = String.valueOf(
                    (easyPay != null && easyPay.get("amount") != null)
                            ? ((Number) easyPay.get("amount")).longValue()
                            : null
            );

            String pkKey = (String) session.getAttribute("PREPAY_PKKEY_" + orderId);
            logger.info("[confirm] pkKey(from session)={}", pkKey);

            // ✅ pkKey 없으면 prepay 실패 / 세션 불일치 / 주문번호 불일치
            if (pkKey == null) {
                jsonObject.put("status", "ABORTED");
                jsonObject.put("message", "prepay pkKey가 없습니다. (prepay 호출/세션/주문번호 확인)");
                return ResponseEntity.status(HttpURLConnection.HTTP_BAD_REQUEST).body(jsonObject);
            }

            RequestPaymentVO vo = new RequestPaymentVO();
            vo.setOrderId(orderId);
            vo.setOrderName(orderName);
            vo.setPaymentKey(paymentKey);
            vo.setEasyAmount(easyAmount);
            vo.setPKkey(pkKey);

            boolean paymentValidationCheck = this.payService.paymentValidationCheck(vo);
            logger.info("[confirm] paymentValidationCheck={}", paymentValidationCheck);

            if (paymentValidationCheck) {
                logger.info("[confirm] >>> paymentSuccessUpdate() 호출 직전 <<<");
                try {
                    this.payService.paymentSuccessUpdate(vo);
                    logger.info("[confirm] >>> paymentSuccessUpdate() 호출 완료 <<<");

                    session.removeAttribute("PREPAY_PKKEY_" + orderId);
                } catch (Exception e) {
                    logger.error("[confirm] paymentSuccessUpdate() 내부 예외 발생", e);
                    jsonObject.put("status", "ABORTED");
                    jsonObject.put("message", "서버 결제 후처리(paymentSuccessUpdate) 실패");
                    return ResponseEntity.status(HttpURLConnection.HTTP_INTERNAL_ERROR).body(jsonObject);
                }
            } else {
                jsonObject.put("status", "ABORTED");
                jsonObject.put("message", "결제 검증 실패(paymentValidationCheck=false)");
                return ResponseEntity.status(HttpURLConnection.HTTP_BAD_REQUEST).body(jsonObject);
            }
        }

        logger.info("응답값 [tosspay] raw response: {}", jsonObject.toJSONString());
        return ResponseEntity.status(code).body(jsonObject);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
