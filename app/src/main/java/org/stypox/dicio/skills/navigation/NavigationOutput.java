package org.stypox.dicio.skills.navigation;

import static org.stypox.dicio.Sections.getSection;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;

import org.dicio.skill.Skill;
import org.dicio.skill.chain.CaptureEverythingRecognizer;
import org.dicio.skill.chain.ChainSkill;
import org.dicio.skill.chain.OutputGenerator;
import org.dicio.skill.standard.StandardRecognizer;
import org.stypox.dicio.R;
import org.stypox.dicio.SectionsGenerated;
import org.stypox.dicio.output.graphical.GraphicalOutputUtils;
import org.stypox.dicio.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NavigationOutput extends OutputGenerator<String> {

    private boolean tryAgain = false;

    @Override
    public void generate(@Nullable final String data) {
        if (StringUtils.isNullOrEmpty(data)) {
            final String msg = ctx().android().getString(R.string.skill_navigation_specify_where);
            ctx().getSpeechOutputDevice().speak(msg);
            ctx().getGraphicalOutputDevice().display(GraphicalOutputUtils.buildSubHeader(
                    ctx().android(), msg));
            tryAgain = true;
            return;
        }
        tryAgain = false;

        final String msg = ctx().android().getString(R.string.skill_navigation_navigating_to, data);
        ctx().getSpeechOutputDevice().speak(msg);
        ctx().getGraphicalOutputDevice().display(GraphicalOutputUtils.buildSubHeader(
                ctx().android(), msg));

        final String uriGeoSimple = String.format(Locale.ENGLISH, "geo:0,0?q=%s", data);
        final Intent launchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uriGeoSimple));
        ctx().android().startActivity(launchIntent);
    }

    @Override
    public List<Skill> nextSkills() {
        if (!tryAgain) {
            return Collections.emptyList();
        }

        return Arrays.asList(
                new ChainSkill.Builder()
                        .recognize(new StandardRecognizer(getSection(SectionsGenerated.navigation)))
                        .process(new NavigationProcessor())
                        .output(new NavigationOutput()),
                new ChainSkill.Builder()
                        .recognize(new CaptureEverythingRecognizer())
                        .process(new NavigationProcessor())
                        .output(new NavigationOutput()));
    }

    @Override
    public void cleanup() {
    }
}