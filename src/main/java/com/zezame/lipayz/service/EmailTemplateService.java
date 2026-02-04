package com.zezame.lipayz.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailTemplateService {
    private final Configuration freemarkerConfig;

    public String getHtmlTemplate(String templateName, Map<String, Object> model) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception e) {
            throw new RuntimeException("Failed to process email template", e);
        }
    }
}
