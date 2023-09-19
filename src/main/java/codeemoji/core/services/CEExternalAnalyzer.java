package codeemoji.core.services;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public final class CEExternalAnalyzer {

    private static volatile CEExternalAnalyzer instance = null;
    private final List<CEExternalService> externalServices = new ArrayList<>();

    private CEExternalAnalyzer() {
        //ler codeemoji.json e verificar sistemas habilitados
        externalServices.add(new GitExternalService());
    }

    public static synchronized CEExternalAnalyzer getInstance() {
        if (instance == null) {
            instance = new CEExternalAnalyzer();
        }
        return instance;
    }

}
