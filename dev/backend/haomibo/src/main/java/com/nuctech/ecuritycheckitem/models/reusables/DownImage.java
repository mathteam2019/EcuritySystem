package com.nuctech.ecuritycheckitem.models.reusables;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DownImage {
    private boolean isEnabledOriginal;
    private boolean isEnabledCartoon;
    private List<String> originalImageList;
    private List<String> cartoonImageList;
}
