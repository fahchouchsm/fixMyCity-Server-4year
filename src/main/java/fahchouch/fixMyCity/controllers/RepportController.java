package fahchouch.fixMyCity.controllers;

import fahchouch.fixMyCity.DTO.CreateRepportDTO;
import fahchouch.fixMyCity.DTO.res.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/repprot")
public class RepportController {

    @PostMapping(value = "/create/repport")
    public ResponseEntity<ApiResponse<Void>> submitRepport(@Valid @RequestBody CreateRepportDTO dto) {
        return ResponseEntity.ok(ApiResponse.success("Report submitted"));
    }
}
