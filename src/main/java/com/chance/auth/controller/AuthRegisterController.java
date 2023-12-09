package com.chance.auth.controller;

import com.chance.auth.contract.OtpService;
import com.chance.auth.domain.req.OtpVerifyRequest;
import com.chance.auth.domain.req.SendOtpRequest;
import com.chance.auth.domain.res.JwtTokenResponse;
import com.chance.auth.domain.res.SendOtpResponse;
import com.chance.auth.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import reactor.util.annotation.Nullable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("auth/register")
@RestController
@RequiredArgsConstructor
public class AuthRegisterController {

    private final OtpService otpService;
    private final UserService userService;

    /**
     * first step of sign-in or sign-up
     *
     * @param sendOtpRequest
     * @return
     */
    @PostMapping("send-otp")
    public Mono<SendOtpResponse> sendSms(@RequestBody SendOtpRequest sendOtpRequest) {
        return otpService.sendSms(sendOtpRequest.msisdn());
    }

    /**
     * second step of sign-in or -sign-up
     *
     * @param otpVerifyRequest
     * @return
     */
    @PostMapping("verify-otp")
    public Mono<JwtTokenResponse> verify(@RequestBody OtpVerifyRequest otpVerifyRequest) {
        return userService.checkVerificationCodeAndRegister(otpVerifyRequest);
    }



    @PostMapping("/uploadExcel")
    public Mono<ResponseEntity<String>> handleExcelFileUpload(
            @RequestPart("file") FilePart filePart) {
        // Dosya adını alma
        String fileName = filePart.filename();

        // Dosyanın geçici olarak kaydedileceği dizin
        Path tempFile = Paths.get("temp", fileName);

        // Dosyayı kaydetme
        Mono<Void> mono = filePart.transferTo(tempFile.toFile());

        // Dosyayı işleme
        return mono.then(Mono.fromCallable(() -> processExcelFile(tempFile)))
                .flatMap(result -> {
                    // İşleme başarılıysa 200 OK döndür
                    return Mono.just(new ResponseEntity<>(result, HttpStatus.OK));
                })
                .onErrorResume(throwable -> {
                    // Hata olması durumunda 500 Internal Server Error döndür
                    return Mono.just(new ResponseEntity<>(throwable.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
                });
    }

    private String processExcelFile(Path filePath) throws IOException {
        // Burada Excel dosyasının işleme algoritmasını implemente edebilirsiniz.
        // Örneğin Apache POI veya başka bir kütüphane kullanarak dosyayı okuyabilirsiniz.

        // Örnek olarak dosyanın adını geri döndürüyoruz.
        return "Excel dosyası işlendi: " + filePath.getFileName();
    }
}
