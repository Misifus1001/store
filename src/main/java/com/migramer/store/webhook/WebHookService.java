package com.migramer.store.webhook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebHookService {

    @Autowired
    private SimpMessagingTemplate template;

    public void notifyProducts(String uuidTienda) {
        template.convertAndSend(buildURL("products", uuidTienda));
    }

    public void notifySesionUser(String uuidTienda) {
        template.convertAndSend(buildURL("users", uuidTienda));
    }

    public void notifyTiendas(String uuidTienda) {
        template.convertAndSend(buildURL("tiendas", uuidTienda));
    }

    public void notifyShoppingCart(String uuidTienda) {
        template.convertAndSend(buildURL("shoppingcart", uuidTienda));
    }

    public void notifyVentas(String uuidTienda) {
        template.convertAndSend(buildURL("ventas", uuidTienda));
    }

    protected String buildURL(String endpoint, String uuidTienda) {
        return "/webhook/" + endpoint + "?uuidTienda=" + uuidTienda;
    }

}