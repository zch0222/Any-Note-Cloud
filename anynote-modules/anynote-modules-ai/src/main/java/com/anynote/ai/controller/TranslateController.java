package com.anynote.ai.controller;

import com.anynote.ai.api.model.bo.Translation;
import com.anynote.ai.api.model.dto.TranslateTextDTO;
import com.anynote.ai.service.TranslateService;
import com.anynote.common.security.annotation.InnerAuth;
import com.anynote.core.utils.ResUtil;
import com.anynote.core.web.model.bo.ResData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("translate")
public class TranslateController {

    @Resource
    private TranslateService translateService;

    @InnerAuth
    @PostMapping("")
    public ResData<List<Translation>> translateText(@RequestBody TranslateTextDTO translateTextDTO) {
        return ResUtil.success(translateService.translateText(translateTextDTO));
    }

}
