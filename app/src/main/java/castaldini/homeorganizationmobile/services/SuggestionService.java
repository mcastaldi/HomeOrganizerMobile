package castaldini.homeorganizationmobile.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import castaldini.homeorganizationmobile.model.House;
import castaldini.homeorganizationmobile.model.Item;
import castaldini.homeorganizationmobile.model.ItemLocation;
import castaldini.homeorganizationmobile.model.ItemLocationContainer;
import castaldini.homeorganizationmobile.model.Room;

/**
 * SuggestionService class that runs on a separate thread and gets the suggested locations to store items from server
 */

public class SuggestionService extends IntentService {

    public static final String SUGGESTION_SERVICE_MESSAGE = "suggestionServiceMessage";
    private static final String TAG = "SuggestionService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * "SuggestionService" Used to name the worker thread, important only for debugging.
     */
    public SuggestionService() {
        super("SuggestionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        List<ItemLocation> suggestedLocations = new ArrayList<>();
        int barcode = -1;
        House theHouse = null;
        //should never be null, but check just in case
        if (intent != null) {
            //get the barcode that's going to be used, as well as the house information
            barcode = intent.getIntExtra("barcode",-1);
            theHouse = intent.getParcelableExtra("house");

        }
        Socket server;
        String searchResponse = null;
        try {
            //connect to server. 10.0.2.2 is the address used in place of localhost since localhost is used by the emulator
            server = new Socket("10.0.2.2", 6066);
            server.setSoTimeout(7000);
            OutputStream outToServer = server.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);

            //message saying what the client will do
            out.writeUTF("getlocdata");

            //get the response to ensure the server is ready
            InputStream inFromServer = server.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println("Server: " + in.readUTF());

            //send the barcode to the server
            out = new DataOutputStream(outToServer);
            String outSearchString = barcode + ",temp,cat";
            out.writeUTF(outSearchString);

            //get the response from the server and close the connection
            in = new DataInputStream(inFromServer);
            searchResponse = in.readUTF();
            System.out.println("Search results: " + searchResponse);
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //check to ensure there was a response and the house was sent from the AddItemActivity class
        if (searchResponse != null && theHouse != null) {
            List<Room> rooms = theHouse.getRooms();

            //Sort through the items returned, the search in the current house to see what
            boolean foundAlready = false;
            String[] splitItems = searchResponse.split("-");
            for (String items : splitItems) {
                if (items.isEmpty())
                    continue;
                String[] itemInfo = items.split(",");
                if (itemInfo[0].isEmpty() || itemInfo[1].isEmpty())
                    continue;
                int tempBarcode = Integer.parseInt(itemInfo[0]);
                Log.i(TAG, itemInfo[0] + ": " + itemInfo[1]);
                Item searchResult = theHouse.findItem(tempBarcode);
                if (searchResult != null) {
                    for (ItemLocation loc : suggestedLocations) {
                        if (loc.getRoomId() == searchResult.getRoomId() && loc.getStorageId() == searchResult.getStorageId())
                            foundAlready = true;
                    }
                    if (!foundAlready)
                        suggestedLocations.add(new ItemLocation(rooms.get(searchResult.getRoomId()).getStorages().get(searchResult.getStorageId())));
                    foundAlready = false;
                }
            }
        }

        //broadcast the results of the sorting so that the AddItemActivity class can receive them.
        Intent messageIntent = new Intent(SUGGESTION_SERVICE_MESSAGE);
        messageIntent.putExtra("suggestedLocations",new ItemLocationContainer(suggestedLocations));
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }
}
