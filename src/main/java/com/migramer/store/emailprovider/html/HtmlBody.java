package com.migramer.store.emailprovider.html;

import org.springframework.stereotype.Component;

import com.migramer.store.emailprovider.models.TypeHtmlBody;

@Component
public class HtmlBody {

    public String getHTMLBody(String message, TypeHtmlBody typeHtmlBody) {

        switch (typeHtmlBody) {
            case RESET_PASSWORD:
                return getHTMLResetPassword(message);
            case SEND_WELCOME:
                return getHTMLWelcomeEmail(message);
            default:
                throw new RuntimeException("HTML no configurado");
        }

    }

    protected String getHTMLResetPassword(String message) {

        String htmlContent = 
            """ 
                <html> 
            """ +
                stylesHTML() +
            """
                <body>
                    <div class="container">
                        <div class="header">
                            <img src='cid:logoImage' alt="Logo">
                            <h1>Restablecimiento de contraseña</h1>
                        </div>
                        <div class="content">
                            <h2>Hola,</h2>
                            <p class="message">%s</p>
                        </div>
                        <div class="footer">
                            © %d - Todos los derechos reservados<br>
                            Este correo fue generado automáticamente, no respondas a este mensaje.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(message, java.time.Year.now().getValue());
        return htmlContent;
    }

    protected String getHTMLWelcomeEmail(String message) {

        String htmlContent = 
            """ 
                <html> 
            """ +
                stylesHTML() +
            """
                <body>
                    <div class="container">
                        <div class="header">
                            <img src='cid:logoImage' alt="Logo">
                            <h1>¡Bienvenido!</h1>
                        </div>
                        <div class="content">
                            <h2>Gracias por unirte a nuestra plataforma</h2>
                            <p class="message">%s</p>
                        </div>
                        <div class="footer">
                            © %d - Todos los derechos reservados<br>
                            Este correo fue generado automáticamente, no respondas a este mensaje.
                        </div>
                    </div>
                </body>
                </html>
            """.formatted(message, java.time.Year.now().getValue());
        return htmlContent;
    }

    protected String stylesHTML() {
        return """
                <head>
                    <style>

                        body {
                            font-family: 'Segoe UI', Arial, sans-serif;
                            background-color: #f4f6f8;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 10px;
                            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
                            overflow: hidden;
                        }
                        .header {
                            background-color: #004aad;
                            color: #ffffff;
                            text-align: center;
                            padding: 25px;
                        }
                        .header img {
                            width: 100px;
                            height: 80px;
                            margin-bottom: 10px;
                        }
                        .content {
                            padding: 30px;
                            text-align: center;
                            color: #333333;
                        }
                        .content h2 {
                            margin-bottom: 10px;
                            color: #004aad;
                        }
                        .message {
                            font-size: 16px;
                            line-height: 1.6;
                            margin-bottom: 25px;
                        }
                        .footer {
                            background-color: #f0f2f5;
                            text-align: center;
                            font-size: 12px;
                            color: #888888;
                            padding: 15px;
                        }
                        .button {
                            display: inline-block;
                            background-color: #004aad;
                            color: #ffffff;
                            text-decoration: none;
                            padding: 12px 25px;
                            border-radius: 6px;
                            font-weight: 600;
                            margin-top: 10px;
                        }
                        .button:hover {
                            background-color: #003b8e;
                        }
                    </style>
                </head>

                """;
    }

}
