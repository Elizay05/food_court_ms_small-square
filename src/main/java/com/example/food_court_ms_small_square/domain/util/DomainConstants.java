package com.example.food_court_ms_small_square.domain.util;

public class DomainConstants {

    private DomainConstants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String URL_VALIDATE_OWNER = "http://localhost:8080/api/v1/users/validate-owner/{documentNumber}";
    public static final String URL_UPDATE_NIT = "http://localhost:8080/api/v1/users/updateNit/{documentNumber}/{nitRestaurant}";
    public static final String URL_GET_PHONE_BY_DOCUMENT = "http://localhost:8080/api/v1/users/getPhoneByDocument/{documentNumber}";
    public static final String URL_SEND_ORDER_READY_MESSAGE = "http://localhost:8082/api/v1/messages/send";


    public static final String ORDER_STATUS_PENDING = "PENDIENTE";
    public static final String ORDER_STATUS_IN_PROGRESS = "EN PREPARACIÓN";
    public static final String ORDER_STATUS_READY = "LISTO";

    public static final String MESSAGE_READY_ORDER = "¡Tu pedido está listo! Usa el PIN de seguridad %s para recogerlo.";
}
