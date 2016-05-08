package com.minecolonies.client.gui;

import com.blockout.Pane;
import com.blockout.controls.Button;
import com.blockout.controls.Label;
import com.blockout.controls.TextField;
import com.blockout.views.ScrollingList;
import com.blockout.views.SwitchView;
import com.minecolonies.MineColonies;
import com.minecolonies.colony.CitizenData;
import com.minecolonies.colony.buildings.BuildingTownHall;
import com.minecolonies.colony.permissions.Permissions;
import com.minecolonies.lib.Constants;
import com.minecolonies.network.messages.PermissionsMessage;
import com.minecolonies.util.LanguageHandler;

import java.util.*;

/**
 * Window for the town hall
 */
public class WindowTownHall extends AbstractWindowSkeleton<BuildingTownHall.View> implements Button.Handler
{
    private static final String BUTTON_INFO          = "info";
    private static final String BUTTON_ACTIONS       = "actions";
    private static final String BUTTON_SETTINGS      = "settings";
    private static final String BUTTON_PERMISSIONS   = "permissions";
    private static final String BUTTON_CITIZENS      = "citizens";
    private static final String BUTTON_RECALL        = "recall";
    private static final String BUTTON_CHANGE_SPEC   = "changeSpec";
    private static final String BUTTON_RENAME        = "rename";
    private static final String BUTTON_ADD_PLAYER    = "addPlayer";
    private static final String INPUT_ADDPLAYER_NAME = "addPlayerName";
    private static final String BUTTON_REMOVE_PLAYER = "removePlayer";
    private static final String BUTTON_PROMOTE       = "promote";
    private static final String BUTTON_DEMOTE        = "demote";
    private static final String VIEW_PAGES           = "pages";
    private static final String PAGE_INFO            = "pageInfo";
    private static final String PAGE_ACTIONS         = "pageActions";
    private static final String PAGE_SETTINGS            = "pageSettings";
    private static final String PAGE_PERMISSIONS         = "pagePermissions";
    private static final String PAGE_CITIZENS            = "pageCitizens";
    private static final String LIST_USERS               = "users";
    private static final String LIST_CITIZENS            = "citizenList";
    private static final String CURRENT_SPEC             = "currentSpec";
    private static final String TOTAL_CITIZENS           = "totalCitizens";
    private static final String UNEMP_CITIZENS           = "unemployedCitizens";
    private static final String BUILDERS                 = "builders";
    private static final String DELIVERY_MAN             = "deliverymen";
    private static final String TOWNHALL_RESOURCE_SUFFIX = ":gui/windowTownHall.xml";
    private BuildingTownHall.View townHall;
    private List<Permissions.Player> users       = new ArrayList<>();
    private List<CitizenData.View>   citizens    = new ArrayList<>();
    private Map<String, String>      tabsToPages = new HashMap<>();
    private Button        lastTabButton;
    private ScrollingList citizenList;
    private ScrollingList userList;

    /**
     * Constructor for the town hall window
     *
     * @param townHall {@link BuildingTownHall.View}
     */
    public WindowTownHall(BuildingTownHall.View townHall)
    {
        super(townHall, Constants.MOD_ID + TOWNHALL_RESOURCE_SUFFIX);
        this.townHall = townHall;

        updateUsers();
        updateCitizens();

        tabsToPages.put(BUTTON_ACTIONS, PAGE_ACTIONS);
        tabsToPages.put(BUTTON_INFO, PAGE_INFO);
        tabsToPages.put(BUTTON_SETTINGS, PAGE_SETTINGS);
        tabsToPages.put(BUTTON_PERMISSIONS, PAGE_PERMISSIONS);
        tabsToPages.put(BUTTON_CITIZENS, PAGE_CITIZENS);

        tabsToPages.keySet().forEach(key -> registerButton(key, this::onTabClicked));
        registerButton(BUTTON_ADD_PLAYER, this::addPlayerCLicked);
        registerButton(BUTTON_RENAME, this::renameClicked);
        registerButton(BUTTON_REMOVE_PLAYER, this::removePlayerClicked);
        registerButton(BUTTON_PROMOTE, this::promoteDemoteClicked);
        registerButton(BUTTON_DEMOTE, this::promoteDemoteClicked);
        registerButton(BUTTON_RECALL,this::doNothing);
        registerButton(BUTTON_CHANGE_SPEC, this::doNothing);
    }

    /**
     * Clears and resets all users
     */
    private void updateUsers()
    {
        users.clear();
        users.addAll(townHall.getColony().getPlayers().values());
        Collections.sort(users, (o1, o2) -> o1.rank.compareTo(o2.rank));
    }

    /**
     * Clears and resets all citizens
     */
    private void updateCitizens()
    {
        citizens.clear();
        citizens.addAll(townHall.getColony().getCitizens().values());
    }

    /**
     * Executed when <code>WindowTownHall</code> is opened.
     * Does tasks like setting buttons
     */
    @Override
    public void onOpened()
    {
        super.onOpened();
        int citizensSize = townHall.getColony().getCitizens().size();

        //TODO - Base these on server-side computed statistics
        int workers     = 0;
        int builders    = 0;
        int deliverymen = 0;

        String numberOfCitizens    = LanguageHandler.format("com.minecolonies.gui.townHall.population.totalCitizens", citizensSize, townHall.getColony().getMaxCitizens());
        String numberOfUnemployed  = LanguageHandler.format("com.minecolonies.gui.townHall.population.unemployed", citizensSize - workers);
        String numberOfBuilders    = LanguageHandler.format("com.minecolonies.gui.townHall.population.builders", builders);
        String numberOfDeliverymen = LanguageHandler.format("com.minecolonies.gui.townHall.population.deliverymen", deliverymen);

        findPaneOfTypeByID(CURRENT_SPEC, Label.class).setLabel("<Industrial>");
        findPaneOfTypeByID(TOTAL_CITIZENS, Label.class).setLabel(numberOfCitizens);
        findPaneOfTypeByID(UNEMP_CITIZENS, Label.class).setLabel(numberOfUnemployed);
        findPaneOfTypeByID(BUILDERS, Label.class).setLabel(numberOfBuilders);
        findPaneOfTypeByID(DELIVERY_MAN, Label.class).setLabel(numberOfDeliverymen);
        findPaneOfTypeByID(VIEW_PAGES, SwitchView.class).setView(PAGE_ACTIONS);

        lastTabButton = findPaneOfTypeByID(BUTTON_ACTIONS, Button.class);
        lastTabButton.setEnabled(false);

        userList = findPaneOfTypeByID(LIST_USERS, ScrollingList.class);
        userList.setDataProvider(new ScrollingList.DataProvider()
        {
            @Override
            public int getElementCount()
            {
                return users.size();
            }

            @Override
            public void updateElement(int index, Pane rowPane)
            {

                Permissions.Player player = users.get(index);
                String rank = player.rank.name();
                rank = Character.toUpperCase(rank.charAt(0)) + rank.toLowerCase().substring(1);
                rowPane.findPaneOfTypeByID("name", Label.class).setLabel(player.name);
                rowPane.findPaneOfTypeByID("rank", Label.class).setLabel(rank);

            }
        });


        citizenList = findPaneOfTypeByID(LIST_CITIZENS, ScrollingList.class);
        citizenList.setDataProvider(new ScrollingList.DataProvider()
        {
            @Override
            public int getElementCount()
            {
                return citizens.size();
            }

            @Override
            public void updateElement(int index, Pane rowPane)
            {

                CitizenData.View citizen = citizens.get(index);

                rowPane.findPaneOfTypeByID("name", Label.class).setLabel(citizen.getName());
                //rowPane.findPaneOfTypeByID("job", Label.class).setLabel("" /* Not working yet */);

            }
        });
    }

    /**
     * Returns the name of a building
     *
     * @return Name of a building
     */
    @Override
    public String getBuildingName()
    {
        return townHall.getColony().getName();
    }

    /**
     * Sets the clicked tab
     *
     * @param button Tab button clicked on
     */
    private void onTabClicked(Button button)
    {
        String page = tabsToPages.get(button.getID());
        findPaneOfTypeByID(VIEW_PAGES, SwitchView.class).setView(page);

        lastTabButton.setEnabled(true);
        button.setEnabled(false);
        lastTabButton = button;
    }

    @Override
    public void onUpdate()
    {
        String currentPage = findPaneOfTypeByID(VIEW_PAGES, SwitchView.class).getCurrentView().getID();
        if (currentPage.equals(PAGE_PERMISSIONS))
        {
            updateUsers();
            window.findPaneOfTypeByID(LIST_USERS, ScrollingList.class).refreshElementPanes();
        }
        else if (currentPage.equals(PAGE_CITIZENS))
        {
            updateCitizens();
            window.findPaneOfTypeByID(LIST_CITIZENS, ScrollingList.class).refreshElementPanes();
        }
    }


    /**
     * Action performed when rename button is clicked
     *
     * @param ignored   Parameter is ignored, since some actions require a button.
     *                  This method does not
     */
    private void renameClicked(Button ignored)
    {
        WindowTownHallNameEntry window = new WindowTownHallNameEntry(townHall.getColony());
        window.open();
    }


    /**
     * Action performed when add player button is clicked
     *
     * @param ignored   Parameter is ignored, since some actions require a button.
     *                  This method does not
     */
    private void addPlayerCLicked(Button ignored)
    {
        TextField input = findPaneOfTypeByID(INPUT_ADDPLAYER_NAME, TextField.class);
        MineColonies.getNetwork().sendToServer(new PermissionsMessage.AddPlayer(townHall.getColony(), input.getText()));
        input.setText("");
    }


    /**
     * Action performed when remove player button is clicked
     *
     * @param button    Button that holds the user clicked on
     */
    private void removePlayerClicked(Button button)
    {
        int row = userList.getListElementIndexByPane(button);
        if (row >= 0 && row < users.size())
        {
            Permissions.Player user = users.get(row);
            if (user.rank != Permissions.Rank.OWNER)
            {
                MineColonies.getNetwork().sendToServer(new PermissionsMessage.RemovePlayer(townHall.getColony(), user.id));
            }
        }
    }


    /**
     * Action performed when popato button is clicked
     *
     * @param button    Button that holds the  user clicked on
     */
    private void promoteDemoteClicked(Button button)
    {
        int row = userList.getListElementIndexByPane(button);
        if (row >= 0 && row < users.size())
        {
            Permissions.Player user = users.get(row);
            Permissions.Rank   newRank;

            if (button.getID().equals(BUTTON_PROMOTE))
            {
                newRank = Permissions.getPromotionRank(user.rank);
            }
            else
            {
                newRank = Permissions.getDemotionRank(user.rank);
            }

            if (newRank != user.rank)
            {
                MineColonies.getNetwork().sendToServer(new PermissionsMessage.SetPlayerRank(townHall.getColony(), user.id, newRank));
            }
        }
    }
}