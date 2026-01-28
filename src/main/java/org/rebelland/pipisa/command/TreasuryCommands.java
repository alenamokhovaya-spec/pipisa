package org.rebelland.pipisa.command;


import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.rebelland.pipisa.command.TreasurySubCreate;

@AutoRegister
public final class TreasuryCommands extends SimpleCommandGroup {
    public TreasuryCommands(){
        super("treasury");
    }
    @Override
    protected void registerSubcommands() {
        registerSubcommand(new TreasurySubCreate());
        registerSubcommand(new TreasurySubDelete());
        registerSubcommand(new TreasurySubMenu());
    }
}
