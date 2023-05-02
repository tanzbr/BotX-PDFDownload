package dev.caua.botxapipdf.globaltec;

import dev.caua.botxapipdf.BotxApiPdfApplication;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Globaltec {

    private static Dotenv dotenv = BotxApiPdfApplication.getDotenv();
    private static String sessionToken = "";
    private static String devToken = dotenv.get("GLOBALTEC_DEV_TOKEN");
    private static String body = dotenv.get("GLOBALTEC_BODY_AUTENTICAR");

    public Globaltec() {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> autenticarApi(), 0, 5, TimeUnit.MINUTES);
    }

    // autenticar api globaltec
    public static void autenticarApi() {
        try {
            URL url = new URL("https://globaltec10api.fwc.cloud:29100/uauAPI/api/v1.0/Autenticador/AutenticarUsuario");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-INTEGRATION-Authorization", devToken);
            con.setRequestProperty("Content-Type", "application/json");
            con.setDoOutput(true);
            con.getOutputStream().write(body.getBytes("UTF-8"));
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                System.out.println("Autenticado com sucesso.");
                sessionToken = content.toString().replaceAll("\"", "");
            } else {
                System.out.println("Erro ao autenticar API.");
                sessionToken = "error";
            }
            con.disconnect();
        } catch (IOException e) {
            System.out.println("Erro ao autenticar API.");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> getBoletosPdf(ArrayList<String> boletos) throws IOException {
        ArrayList<String> boletosBase64 = new ArrayList<>();
        for (String id : boletos) {
            String[] idFormated = id.split("-");
            String codBanco = idFormated[0];
            String seuNumero = idFormated[1];
            boletosBase64.add(getBoletoBase64(codBanco, seuNumero));
        }
        return boletosBase64;
    }


    public static String getBoletoBase64(String codBanco, String seuNumero) throws IOException {
        if (sessionToken.equals("error")) {
            System.out.println("Erro ao autenticar.");
            return "Erro ao autenticar";
        }
        HttpURLConnection con1 = null;
        JSONObject responseObj = null;

        URL url1 = new URL("https://globaltec10api.fwc.cloud:29100/uauAPI/api/v1.0/BoletoServices/GerarPDFBoleto");
        con1 = (HttpURLConnection) url1.openConnection();
        con1.setRequestMethod("POST");
        con1.setRequestProperty("X-INTEGRATION-Authorization", devToken);
        con1.setRequestProperty("Authorization", sessionToken);
        con1.setRequestProperty("Content-Type", "application/json");

        String requestBody1 = "{\"cod_banco\": \""+ codBanco +"\", \"seu_numero\": \""+ seuNumero +"\", \"ocultar_dados_pessoais\": \"false\"}";

        con1.setDoOutput(true);
        DataOutputStream out1 = new DataOutputStream(con1.getOutputStream());
        out1.writeBytes(requestBody1);
        out1.flush();
        out1.close();

        int responseCode1 = con1.getResponseCode();
        if (responseCode1 == HttpURLConnection.HTTP_UNAUTHORIZED) {
            autenticarApi();
            JSONObject responseReturn = new JSONObject();
            return "Erro";
        } else if (responseCode1 == HttpURLConnection.HTTP_OK) {
            BufferedReader in1 = new BufferedReader(new InputStreamReader(con1.getInputStream()));
            String response1;
            StringBuffer response1Content = new StringBuffer();
            while ((response1 = in1.readLine()) != null) {
                response1Content.append(response1);
            }
            in1.close();

            if (response1Content.toString().equals("")) {
                return "Erro";
            }

            return response1Content.toString().replaceAll("\"", "");
        }
        return "Erro";
    }


}
