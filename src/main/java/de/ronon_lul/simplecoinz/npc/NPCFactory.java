package de.ronon_lul.simplecoinz.npc;

import de.ronon_lul.simplecoinz.gui.inventories.Merchants.Blacksmith;
import de.ronon_lul.simplecoinz.gui.inventories.Merchants.BlockMerchant;
import de.ronon_lul.simplecoinz.gui.inventories.bank.OverviewBank;
import de.ronon_lul.simplecoinz.npc.api.NPCInfos;
import org.bukkit.Location;

/**
 * a Factory to build different NPC's
 */
public class NPCFactory {

    /**
     * Generate an NPC with the help of the Type
     *
     * @param type the Type of NPC (e.g. BANK)
     * @param loc  the location where the NPC should be
     * @return the ready NPC
     */
    public static NPCInfos generateInfoByType(NPCTypes type, Location loc) {
        switch (type) {
            case BANK:
                return bank(loc);
            case BLOCKMERCHANT:
                return blockMerchant(loc);
            case BLACKSMITH:
                return blackSmith(loc);
            case UNKNOWN:
            default:
                return null;
        }
    }

    /**
     * returns an BANK npc, which opens the BANK GUI on click
     *
     * @param location the location where the NPC should be
     * @return the NPCInfos
     */
    private static NPCInfos bank(Location location) {
        String name = "Bank guy";
        return NPCInfos.builder()
                .name(name)
                .hideNameTag(false)
                .texture("eyJ0aW1lc3RhbXAiOjE1NTIwNzQ1Mjg4NDYsInByb2ZpbGVJZCI6IjU2ZTQyNDMwODg2ZjQxMTk5YjRjNWNkZWI2YTc0YjdmIiwicHJvZmlsZU5hbWUiOiJzcGVjdHJlcGhvZW5peCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg2MTM0M2RmM2E4MGFjMGEyOGFhZGU4Y2JmZDE0MTNkM2U5NTEyYmJiNjdmOTBlMGYzY2E2NzYxMTMwN2FiZCJ9fX0=")
                .signature("pbigzNFl/IIvDex6C2kadsy+XzVeZAT7RDCM1Q44ve7XyOF7mKgwxbaeD43y3aN10jScN+cEc6yYcWvA6vf/q1gDmDQ8qiSYtFQeDbxFVGcvz7ouiwSIFMNGhM3gUGW6aODm2COlqN0/ewVEC1Bb/FeZpYTG7jzyIPkbmc3j1MXRJ1T2vp1q5iVqF3fMhcqIPxPSswyAJ7kStg/mcx4GB22T2gxMTtSyVyzHKhT+S/pXaw+iw9ksiI1G12VB2Qz1u+ZKr+20cdTtnMbAWLtdoDP6Tw+CcNw+HecjfUmRU4wMPj742wFOh9MW2E4JYHBR6OBff9+m1tVZjy2fA5HYvISnDpJBzetUU+3U8LbTqCwILOcwJh1+bKqfM1tnF0o1E55/5IcSKXOPReG5GLLthiD4XYlGDECpOjAxZcRDkQZzj03+6b7diGt96LW8rDDpFyOYORaWjmzOvppY/49g8ikexFDV+DxaOJcnLZv51hWbSib0fPEJJK5014Vro3thcmYqBbZL3bOKVgJwNFXcq6DB6rfqHZpInnBOAZ9TtwF1ddTJp9tDicMWDIDVGctUzqQoTwKJtgIbgpn2wfZw5jQIkzILlyD3g7dPc+4QFsR9dIKBQVCT7nMKu1QZpcvpI/G/pXSweWHIa1JnWPCjAValOFJyA7CvJT85caXZXh8=")
                .location(location)
                .type(NPCTypes.BANK)
                .handler(event -> {
                    if (event.getClickedNPC().getName().equalsIgnoreCase(name)) {
                        new OverviewBank(event.getWhoClicked()).open();
                    }
                })
                .build();
    }

    /**
     * returns an BlockMerchant npc, which opens the Block_buy menu on click
     *
     * @param location the location where the NPC should be
     * @return the NpcInfo
     */
    private static NPCInfos blockMerchant(Location location) {
        String name = "BlockMerchant";
        return NPCInfos.builder()
                .name(name)
                .hideNameTag(false)
                .texture("ewogICJ0aW1lc3RhbXAiIDogMTYwNTc1MjYxNTEwOSwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZDUxZGY0NjA2MWRjMWVjMDVhYjViY2E0ZjI4YjYwNDU0MmExMjQwNWFmZWE4MGFmNTQzOTAzNGIxZTI5YWUxIgogICAgfQogIH0KfQ==")
                .signature("Mh458Afez1Dv0X8v3/pEh/dh6IJBey2LlDOL3r9ctJIVzNW+gcCen8Ht+59S2ao4MmPrecr7n6HjYOC6AyJUro4DQ2KkxPCFAEzA7QNzEQmjtqt+KFLMowMlnnrCpY3qr90/5iD2JEzaGB3QUuDJK3ZJj8PvSMYIqQUCT/lyzLKaP1o4XJqKbisQAXDnfZWAsF1W1yxeX6b3+bMd29y8zSCtoRNtTd2sizlqi4XyP93IWS8qPepxNwxsSya983sWJ4zep4LPcH5r7EEcl+WOYA0vQRCCDu9zsJa/XtR0QpizUT5a0rquF3SruSq+kgAqa/T7JTdSSX5OUZwFcGriVeuHV0VZhyqnU+qGO4Jp9JADggdKFECV2jNmILYgAYAXojUKFo4Brxvg/qWYCeMR90z+Z+8GKpwvrGD1XP0OWYNWieIoVuF4t4NuyafPNqtZdABAnrfgyVTO1WkXMv+rB2mHcW7+gAec2cJek3qKk8rqZ1+s5Q5LaiRG9Ysz8iZR+7OaBAqvrt9h1N+OXVazvcwb/VTOEIuqGf0ZW2JwtRUSW3OESrw2JuX32P1LYotI8jNhcNDbcmo0/TLAoo0A4bSgrOlx/v4LTUGICTcWmxvprZ6Sr4GROm7ZoXea5xw9E6PbmnK/VdfKjlpQHV+zQEeLuL+75DYOHDCVL0TUYY0=")
                .location(location)
                .type(NPCTypes.BLOCKMERCHANT)
                .handler(event -> {
                    if (event.getClickedNPC().getName().equalsIgnoreCase(name)) {
                        new BlockMerchant(event.getWhoClicked()).open();
                    }
                })
                .build();
    }


    /**
     * returns an BlockMerchant npc, which opens the Sword_buy menu on click
     *
     * @param location the location where the NPC should be
     * @return the NpcInfo
     */
    private static NPCInfos blackSmith(Location location) {
        String name = "blacksmith";
        return NPCInfos.builder()
                .name(name)
                .hideNameTag(false)
                .texture("ewogICJ0aW1lc3RhbXAiIDogMTYwNTc1MjYxNTEwOSwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iZDUxZGY0NjA2MWRjMWVjMDVhYjViY2E0ZjI4YjYwNDU0MmExMjQwNWFmZWE4MGFmNTQzOTAzNGIxZTI5YWUxIgogICAgfQogIH0KfQ==")
                .signature("Mh458Afez1Dv0X8v3/pEh/dh6IJBey2LlDOL3r9ctJIVzNW+gcCen8Ht+59S2ao4MmPrecr7n6HjYOC6AyJUro4DQ2KkxPCFAEzA7QNzEQmjtqt+KFLMowMlnnrCpY3qr90/5iD2JEzaGB3QUuDJK3ZJj8PvSMYIqQUCT/lyzLKaP1o4XJqKbisQAXDnfZWAsF1W1yxeX6b3+bMd29y8zSCtoRNtTd2sizlqi4XyP93IWS8qPepxNwxsSya983sWJ4zep4LPcH5r7EEcl+WOYA0vQRCCDu9zsJa/XtR0QpizUT5a0rquF3SruSq+kgAqa/T7JTdSSX5OUZwFcGriVeuHV0VZhyqnU+qGO4Jp9JADggdKFECV2jNmILYgAYAXojUKFo4Brxvg/qWYCeMR90z+Z+8GKpwvrGD1XP0OWYNWieIoVuF4t4NuyafPNqtZdABAnrfgyVTO1WkXMv+rB2mHcW7+gAec2cJek3qKk8rqZ1+s5Q5LaiRG9Ysz8iZR+7OaBAqvrt9h1N+OXVazvcwb/VTOEIuqGf0ZW2JwtRUSW3OESrw2JuX32P1LYotI8jNhcNDbcmo0/TLAoo0A4bSgrOlx/v4LTUGICTcWmxvprZ6Sr4GROm7ZoXea5xw9E6PbmnK/VdfKjlpQHV+zQEeLuL+75DYOHDCVL0TUYY0=")
                .location(location)
                .type(NPCTypes.BLACKSMITH)
                .handler(event -> {
                    if (event.getClickedNPC().getName().equalsIgnoreCase(name)) {
                        new Blacksmith(event.getWhoClicked()).open();
                    }
                })
                .build();
    }

}
