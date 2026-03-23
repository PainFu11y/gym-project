package com.gym_project.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SwaggerUiController {

    @GetMapping(value = "/swagger-ui.html", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String swaggerUi() {
        return "<!DOCTYPE html>" +
                "<html><head>" +
                "<title>Gym Project API</title>" +
                "<meta charset=\"utf-8\"/>" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" +
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui.css\">" +
                "</head><body>" +
                "<div id=\"swagger-ui\"></div>" +
                "<script src=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui-bundle.js\"></script>" +
                "<script src=\"https://unpkg.com/swagger-ui-dist@5/swagger-ui-standalone-preset.js\"></script>" +
                "<script>" +
                "window.onload = function() {" +
                "  SwaggerUIBundle({" +
                "    url: '/v2/api-docs'," +
                "    dom_id: '#swagger-ui'," +
                "    presets: [SwaggerUIBundle.presets.apis, SwaggerUIStandalonePreset]," +
                "    layout: 'StandaloneLayout'," +
                "    deepLinking: true" +
                "  })" +
                "}" +
                "</script>" +
                "</body></html>";
    }
}