package com.anynote.common.green.enums;


import lombok.Getter;

@Getter
public enum GreenLabel {

    NON_LABEL("nonLabel", "正常内容"),

    PORNOGRAPHIC("pornographic", "疑似色情内容"),

    SEXUAL("sexual", "疑似性相关内容"),

    POLITICAL("political", "政治相关内容"),

    VIOLENT("violent", "疑似极端组织、极端主义、武器弹药内容"),

    CONTRABAND("contraband", "疑似毒品、赌博、违禁行为、违禁工具"),

    INAPPROPRIATE("inappropriate", "疑似歧视、不良价值观、攻击辱骂、低俗用语、封建迷信、无意义灌水内容"),

    PT("pt", "疑似站外引流、网赚兼职、引流广告号内容"),

    RELIGION("religion", "疑似宗教内容"),

    CUSTOMIZED("customized", "自定义词库");

    /**
     * 标签
     */
    final private String label;

    /**
     * 中文含义
     */
    final private String chineseMeaning;


    GreenLabel(String label, String chineseMeaning) {
        this.label = label;
        this.chineseMeaning = chineseMeaning;
    }


}
