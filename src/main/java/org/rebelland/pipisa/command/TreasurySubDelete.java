package org.rebelland.pipisa.command;

import org.mineacademy.fo.command.SimpleSubCommand;
import org.rebelland.pipisa.database.TreasuryRepository;

public class TreasurySubDelete extends SimpleSubCommand {
    public TreasurySubDelete() {
        super("delete");
        setMinArguments(1); // Требуется минимум 1 аргумент (название)
        setUsage("<название>");
    }
    @Override
    protected void onCommand(){
        // Получение названия казны из параметра
        final String treasuryName = args[0];
        // Ваша логика создания казны
        boolean delete = TreasuryRepository.getInstance().deleteTreasury(treasuryName);
        if(delete){
            tellSuccess("Казна '" + treasuryName + "' удалена!");}
        else {
            returnTell("&cКазна с названием '" + treasuryName + "' не существует!");
        }

    }
}