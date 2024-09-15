package fr.kinjer.kjda;

import net.dv8tion.jda.api.JDA;

public class KJDAImpl extends KJDA {

    private final JDA jda;

    private KJDAImpl(JDA jda) {
        this.jda = jda;
    }

    @Override
    public JDA getJDA() {
        return null;
    }
}
