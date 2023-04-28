package dev.caua.botxapipdf.api;

import dev.caua.botxapipdf.globaltec.Globaltec;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping(value = "/baixarPdf")
    public ResponseEntity<Resource> baixarPdf(@RequestParam("id") String id, @RequestParam("name") String name) throws IOException {

        String[] idFormated = id.split("-");

        String content = Globaltec.getBoletoBase64(idFormated[0], idFormated[1]);;

        if (content.equalsIgnoreCase("Erro ao autenticar") || content.equals("Erro")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        System.out.println(content);
        byte[] decoder = Base64.getDecoder().decode(content);
        InputStream is = new ByteArrayInputStream(decoder);
        InputStreamResource resource = new InputStreamResource(is);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition disposition = ContentDisposition.attachment().filename(name+".pdf").build();
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    @GetMapping(value = "/baixarPdfVencidos")
    public ResponseEntity<Resource> baixarPdfVencidos(@RequestParam("id") String idList, @RequestParam("name") String name) throws IOException {

        String[] listaIds = idList.split("_");

        ArrayList<String> listaIds2 = new ArrayList<>(Arrays.asList(listaIds));

        ArrayList<String> pdfsBase64 = Globaltec.getBoletosPdf(listaIds2);

        List<InputStream> inputsPdfs = new ArrayList<>();

        for (String pdfBase64 : pdfsBase64) {
            byte[] decoder = Base64.getDecoder().decode(pdfBase64);
            InputStream is = new ByteArrayInputStream(decoder);
            inputsPdfs.add(is);
        }

        PDFMergerUtility pdfMergerUtility = new PDFMergerUtility();
        pdfMergerUtility.addSources(inputsPdfs);

        ByteArrayOutputStream colDocOutputstream = new ByteArrayOutputStream();

        pdfMergerUtility.setDestinationStream(colDocOutputstream);
        pdfMergerUtility.mergeDocuments(MemoryUsageSetting.setupTempFileOnly());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition disposition = ContentDisposition.attachment().filename(name+".pdf").build();
        headers.setContentDisposition(disposition);

        InputStream is = new ByteArrayInputStream(colDocOutputstream.toByteArray());
        InputStreamResource resource = new InputStreamResource(is);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        /*

        String content = Globaltec.getBoletoBase64(idFormated[0], idFormated[1]);;

        if (content.equalsIgnoreCase("Erro ao autenticar") || content.equals("Erro")) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        System.out.println(content);
        byte[] decoder = Base64.getDecoder().decode(content);
        InputStream is = new ByteArrayInputStream(decoder);
        InputStreamResource resource = new InputStreamResource(is);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        ContentDisposition disposition = ContentDisposition.attachment().filename(name+".pdf").build();
        headers.setContentDisposition(disposition);

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);*/
    }

}
