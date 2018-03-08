package org.oscelot.bb.auth.autosignon.service;

import blackboard.platform.intl.BbResourceBundle;
import blackboard.platform.ultra.UltraUiManager;

public class UltraUiService {
    private UltraUiManager ultraUiManager;

    public boolean isUltraUiEnabled() {
        return ultraUiManager.isUltraUiEnabled();
    }

    public void setUltraUiEnabled(boolean b) {
        ultraUiManager.setUltraUiEnabled(b);
    }

    public String getUltraTextForBundleKey(BbResourceBundle bbResourceBundle, String s) {
        return ultraUiManager.getUltraTextForBundleKey(bbResourceBundle, s);
    }

    public boolean isUltraUiDecisionEnabled() {
        return ultraUiManager.isUltraUiDecisionEnabled();
    }

    public void setUltraUiManager(UltraUiManager ultraUiManager) {
        this.ultraUiManager = ultraUiManager;
    }
}
