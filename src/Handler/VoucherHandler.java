package Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import model.Voucher;
import Service.VoucherService;
import Tugas2.Response;
import Response.ApiResponse;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class VoucherHandler {

    private static final VoucherService voucherService = new VoucherService();
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean handle(HttpExchange httpExchange, String method, String path,
                                 Map<String, Object> requestBody, Response response) {

        try {
            if (path.equals("/vouchers") && "GET".equals(method)) {
                return handleGetAllVouchers(response);
            }

            if (path.matches("/vouchers/\\d+") && "GET".equals(method)) {
                int id = extractIdFromPath(path);
                return handleGetVoucherById(id, response);
            }

            if (path.equals("/vouchers") && "POST".equals(method)) {
                return handleCreateVoucher(requestBody, response);
            }

            if (path.matches("/vouchers/\\d+") && "PUT".equals(method)) {
                int id = extractIdFromPath(path);
                return handleUpdateVoucher(id, requestBody, response);
            }

            if (path.matches("/vouchers/\\d+") && "DELETE".equals(method)) {
                int id = extractIdFromPath(path);
                return handleDeleteVoucher(id, response);
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            try {
                ApiResponse<Object> errorResponse = ApiResponse.error("Internal server error: " + e.getMessage());
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return true;
        }
    }

    private static boolean handleGetAllVouchers(Response response) throws Exception {
        try {
            List<Voucher> vouchers = voucherService.getAllVouchers();
            List<Map<String, Object>> voucherMaps = vouchers.stream()
                    .map(VoucherHandler::voucherToMap)
                    .collect(Collectors.toList());

            ApiResponse<List<Map<String, Object>>> apiResponse =
                    ApiResponse.success("Vouchers retrieved successfully", voucherMaps);

            response.setBody(objectMapper.writeValueAsString(apiResponse));
            response.send(HttpURLConnection.HTTP_OK);
            return true;
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to retrieve vouchers: " + e.getMessage());
            response.setBody(objectMapper.writeValueAsString(errorResponse));
            response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return true;
        }
    }

    private static boolean handleGetVoucherById(int id, Response response) throws Exception {
        try {
            Voucher voucher = voucherService.getVoucherById(id);

            if (voucher == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Voucher with ID " + id + " not found");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_NOT_FOUND);
                return true;
            }

            ApiResponse<Map<String, Object>> apiResponse =
                    ApiResponse.success("Voucher retrieved successfully", voucherToMap(voucher));
            response.setBody(objectMapper.writeValueAsString(apiResponse));
            response.send(HttpURLConnection.HTTP_OK);
            return true;
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to retrieve voucher: " + e.getMessage());
            response.setBody(objectMapper.writeValueAsString(errorResponse));
            response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return true;
        }
    }

    private static boolean handleCreateVoucher(Map<String, Object> requestBody, Response response) throws Exception {
        try {
            if (requestBody == null || requestBody.isEmpty()) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Request body is required");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            String validationError = validateVoucherData(requestBody, false);
            if (validationError != null) {
                ApiResponse<Object> errorResponse = ApiResponse.error(validationError);
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            String code = (String) requestBody.get("code");
            if (voucherService.isVoucherCodeExists(code, null)) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Voucher code already exists");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            Voucher voucher = createVoucherFromRequestBody(requestBody);

            Voucher createdVoucher = voucherService.createVoucher(voucher);

            ApiResponse<Map<String, Object>> apiResponse =
                    ApiResponse.success("Voucher created successfully", voucherToMap(createdVoucher));
            response.setBody(objectMapper.writeValueAsString(apiResponse));
            response.send(HttpURLConnection.HTTP_CREATED);
            return true;
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to create voucher: " + e.getMessage());
            response.setBody(objectMapper.writeValueAsString(errorResponse));
            response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return true;
        }
    }

    private static boolean handleUpdateVoucher(int id, Map<String, Object> requestBody, Response response) throws Exception {
        try {
            Voucher existingVoucher = voucherService.getVoucherById(id);
            if (existingVoucher == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Voucher with ID " + id + " not found");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_NOT_FOUND);
                return true;
            }

            if (requestBody == null || requestBody.isEmpty()) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Request body is required");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            String validationError = validateVoucherData(requestBody, true);
            if (validationError != null) {
                ApiResponse<Object> errorResponse = ApiResponse.error(validationError);
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            String code = (String) requestBody.get("code");
            if (voucherService.isVoucherCodeExists(code, id)) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Voucher code already exists");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_BAD_REQUEST);
                return true;
            }

            Voucher voucher = createVoucherFromRequestBody(requestBody);

            Voucher updatedVoucher = voucherService.updateVoucher(id, voucher);

            ApiResponse<Map<String, Object>> apiResponse =
                    ApiResponse.success("Voucher updated successfully", voucherToMap(updatedVoucher));
            response.setBody(objectMapper.writeValueAsString(apiResponse));
            response.send(HttpURLConnection.HTTP_OK);
            return true;
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to update voucher: " + e.getMessage());
            response.setBody(objectMapper.writeValueAsString(errorResponse));
            response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return true;
        }
    }

    private static boolean handleDeleteVoucher(int id, Response response) throws Exception {
        try {
            Voucher existingVoucher = voucherService.getVoucherById(id);
            if (existingVoucher == null) {
                ApiResponse<Object> errorResponse = ApiResponse.error("Voucher with ID " + id + " not found");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_NOT_FOUND);
                return true;
            }

            boolean deleted = voucherService.deleteVoucher(id);

            if (deleted) {
                ApiResponse<Object> apiResponse = ApiResponse.success("Voucher deleted successfully", null);
                response.setBody(objectMapper.writeValueAsString(apiResponse));
                response.send(HttpURLConnection.HTTP_OK);
            } else {
                ApiResponse<Object> errorResponse = ApiResponse.error("Failed to delete voucher");
                response.setBody(objectMapper.writeValueAsString(errorResponse));
                response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            }

            return true;
        } catch (Exception e) {
            ApiResponse<Object> errorResponse = ApiResponse.error("Failed to delete voucher: " + e.getMessage());
            response.setBody(objectMapper.writeValueAsString(errorResponse));
            response.send(HttpURLConnection.HTTP_INTERNAL_ERROR);
            return true;
        }
    }

    private static String validateVoucherData(Map<String, Object> requestBody, boolean isUpdate) {
        // Check required fields
        if (!requestBody.containsKey("code") || requestBody.get("code") == null) {
            return "Voucher code is required";
        }

        if (!requestBody.containsKey("description") || requestBody.get("description") == null) {
            return "Voucher description is required";
        }

        if (!requestBody.containsKey("discount") || requestBody.get("discount") == null) {
            return "Voucher discount is required";
        }

        if (!requestBody.containsKey("start_date") || requestBody.get("start_date") == null) {
            return "Voucher start date is required";
        }

        if (!requestBody.containsKey("end_date") || requestBody.get("end_date") == null) {
            return "Voucher end date is required";
        }

        String code = (String) requestBody.get("code");
        if (code.trim().isEmpty()) {
            return "Voucher code cannot be empty";
        }

        if (code.length() < 3 || code.length() > 20) {
            return "Voucher code must be between 3 and 20 characters";
        }

        String description = (String) requestBody.get("description");
        if (description.trim().isEmpty()) {
            return "Voucher description cannot be empty";
        }

        Object discountObj = requestBody.get("discount");
        double discount;
        try {
            if (discountObj instanceof Integer) {
                discount = ((Integer) discountObj).doubleValue();
            } else if (discountObj instanceof Double) {
                discount = (Double) discountObj;
            } else {
                discount = Double.parseDouble(discountObj.toString());
            }
        } catch (NumberFormatException e) {
            return "Invalid discount format";
        }

        if (discount < 0 || discount > 100) {
            return "Discount must be between 0 and 100";
        }

        try {
            String startDateStr = (String) requestBody.get("start_date");
            String endDateStr = (String) requestBody.get("end_date");

            LocalDateTime startDate = LocalDateTime.parse(startDateStr, FORMATTER);
            LocalDateTime endDate = LocalDateTime.parse(endDateStr, FORMATTER);

            if (endDate.isBefore(startDate) || endDate.isEqual(startDate)) {
                return "End date must be after start date";
            }

        } catch (DateTimeParseException e) {
            return "Invalid date format. Use yyyy-MM-dd HH:mm:ss format";
        }

        return null;
    }

    private static Voucher createVoucherFromRequestBody(Map<String, Object> requestBody) {
        Voucher voucher = new Voucher();

        voucher.setCode(((String) requestBody.get("code")).trim());
        voucher.setDescription(((String) requestBody.get("description")).trim());

        Object discountObj = requestBody.get("discount");
        if (discountObj instanceof Integer) {
            voucher.setDiscount(((Integer) discountObj).doubleValue());
        } else if (discountObj instanceof Double) {
            voucher.setDiscount((Double) discountObj);
        } else {
            voucher.setDiscount(Double.parseDouble(discountObj.toString()));
        }

        String startDateStr = (String) requestBody.get("start_date");
        String endDateStr = (String) requestBody.get("end_date");

        voucher.setStartDate(LocalDateTime.parse(startDateStr, FORMATTER));
        voucher.setEndDate(LocalDateTime.parse(endDateStr, FORMATTER));

        return voucher;
    }

    private static int extractIdFromPath(String path) {
        Pattern pattern = Pattern.compile("/vouchers/(\\d+)");
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        throw new IllegalArgumentException("Invalid path format");
    }

    private static Map<String, Object> voucherToMap(Voucher voucher) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", voucher.getId());
        map.put("code", voucher.getCode());
        map.put("description", voucher.getDescription());
        map.put("discount", voucher.getDiscount());
        map.put("start_date", voucher.getStartDate().format(FORMATTER));
        map.put("end_date", voucher.getEndDate().format(FORMATTER));
        return map;
    }
}