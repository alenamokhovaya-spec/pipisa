package org.rebelland.pipisa.command;

import org.mineacademy.fo.command.SimpleSubCommand;
import org.rebelland.pipisa.database.TreasuryRepository;

public class TreasurySubCreate extends SimpleSubCommand {
    public TreasurySubCreate() {
        super("create");
        setMinArguments(1); // Требуется минимум 1 аргумент (название)
        setUsage("<название>");
    }
    @Override
    protected void onCommand(){
        // Получение названия казны из параметра
        final String treasuryName = args[0];



        // Проверка аргумента
        checkNotNull(treasuryName, "Укажите название казны");

        // Ваша логика создания казны
        boolean created = TreasuryRepository.getInstance().createTreasury(treasuryName);
        if(created){
            tellSuccess("Казна '" + treasuryName + "' создана!");}
        else {
            returnTell("&cКазна с названием '" + treasuryName + "' уже существует!");
        }

    }
}
