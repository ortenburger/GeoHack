package eu.quelltext.mundraub.plant;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import eu.quelltext.mundraub.R;
import eu.quelltext.mundraub.activities.LoginActivity;
import eu.quelltext.mundraub.api.API;
import eu.quelltext.mundraub.error.Logger;

public class PlantOnlineState {

    public static final String JSON_CLASS = "type";
    public static final String JSON_CLASS_OFFLINE = "offline";
    public static final String JSON_CLASS_ONLINE = "online";
    public static final String JSON_ID = "id";
    public static final String JSON_API = "api";

    public interface OnlineAction {

        boolean mustLogin();
        boolean canCreate();
        boolean canUpdate();
        boolean hasURL();
        boolean canDelete();

        void create(API.Callback cb);
        void update(API.Callback cb);
        String getURL();
        void delete(API.Callback cb);
        JSONObject toJSON() throws JSONException;

        void publishedWithId(String s, API api);

        boolean isPublished();

        API api();

        void openLoginFrom(Context context);
    }

    static private class OfflineState implements OnlineAction {

        private final Plant plant;

        private OfflineState(Plant plant) {
            this.plant = plant;
        }

        public API api() {
            return API.instance();
        }

        @Override
        public void openLoginFrom(Context context) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }

        @Override
        public boolean mustLogin() {
            return !api().isLoggedIn();
        }

        @Override
        public boolean canCreate() {
            return plant.hasRequiredFieldsFilled() && !mustLogin();
        }

        @Override
        public boolean canUpdate() {
            return false;
        }

        @Override
        public boolean hasURL() {
            return false;
        }

        @Override
        public boolean canDelete() {
            return false;
        }

        @Override
        public void create(final API.Callback cb) {
            if (!canCreate()) {
                cb.onFailure(R.string.operation_not_permitted);
                return;
            }
            api().addPlant(plant, cb);
        }

        @Override
        public void update(API.Callback cb) {
            cb.onFailure(R.string.operation_not_permitted);
        }
        @Override
        public String getURL() {
            return null;
        }
        @Override
        public void delete(API.Callback cb) {
            cb.onFailure(R.string.operation_not_permitted);
        }
        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(JSON_CLASS, JSON_CLASS_OFFLINE);
            return json;
        }

        @Override
        public void publishedWithId(String id, API api) {
            this.plant.setOnline(new OnlineState(plant, id, api));
        }

        @Override
        public boolean isPublished() {
            return false;
        }

        private static OnlineAction fromJSON(Plant plant, JSONObject json) {
            return new OfflineState(plant);
        }
    }

    static private class OnlineState implements OnlineAction {
        private final Plant plant;
        private final String id;
        private final API api;

        private OnlineState(Plant plant, String id, API api) {
            this.plant = plant;
            this.id = id;
            this.api = api;
        }

        public API api() {
            return api;
        }

        @Override
        public void openLoginFrom(Context context) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra(LoginActivity.ARG_API_ID, api.id());
            context.startActivity(intent);
        }

        @Override
        public boolean mustLogin() {
            return !api.isLoggedIn();
        }

        @Override
        public boolean canCreate() {
            return false;
        }

        @Override
        public boolean canUpdate() {
            return api.isLoggedIn() && api.canUpdate();
        }

        @Override
        public boolean hasURL() {
            return getURL() != null;
        }

        @Override
        public boolean canDelete() {
            return api.isLoggedIn();
        }

        @Override
        public void create(API.Callback cb) {
            cb.onFailure(R.string.operation_not_permitted);
        }

        @Override
        public void update(API.Callback cb) {
            api.updatePlant(plant, id, cb);
        }

        @Override
        public String getURL() {
            return api.getPlantUrl(id);
        }

        @Override
        public void delete(final API.Callback cb) {
            api.deletePlant(id, new API.Callback() {
                @Override
                public void onSuccess() {
                    plant.setOnline(new OfflineState(plant));
                    cb.onSuccess();
                }

                @Override
                public void onFailure(int errorResourceString) {
                    cb.onFailure(errorResourceString);
                }
            });
        }

        @Override
        public JSONObject toJSON() throws JSONException {
            JSONObject json = new JSONObject();
            json.put(JSON_CLASS, JSON_CLASS_ONLINE);
            json.put(JSON_ID, id);
            json.put(JSON_API, api.id());
            return json;
        }

        @Override
        public void publishedWithId(final String newId, final API api) {
            if (newId.equals(id)) {
                return;
            }
            delete(new API.Callback() {
                @Override
                public void onSuccess() {
                    plant.setOnline(new OnlineState(plant, newId, api));
                }

                @Override
                public void onFailure(int errorResourceString) {
                    final Logger.Log log = Logger.newFor("PlantOnlineState");
                    log.e("PLANT PUBLISHED TWICE", "The plant " + plant.getId() +
                            " was published twice under id " +
                            newId + " and " + id + ". Could not delete " + id +
                            ". Trying to delete " + newId);
                    OnlineState s = new OnlineState(plant, newId, api);
                    s.delete(new API.Callback() {
                        @Override
                        public void onSuccess() {
                        }

                        @Override
                        public void onFailure(int errorResourceString) {
                            log.e("PLANT PUBLISHED TWICE", "The plant " + plant.getId() +
                                    " was published twice under id " +
                                    newId + " and " + id + ". Could not delete either one of them.");
                        }
                    });
                }
            });
        }

        @Override
        public boolean isPublished() {
            return true;
        }

        private static OnlineAction fromJSON(Plant plant, JSONObject json) throws JSONException {
            String onlineId = json.getString(JSON_ID);
            API api = API.MUNDRAUB; // legacy: API not set, 2018-09-14
            if (json.has(JSON_API)) {
                api = API.fromId(json.getString(JSON_API));
            }
            return new OnlineState(plant, onlineId, api);
        }
    }

    public static OfflineState getOfflineState(Plant plant) {
        return new OfflineState(plant);
    }
    public static OnlineAction fromJSON(Plant plant, JSONObject json) throws JSONException {
        String state = json.getString(JSON_CLASS);
        if (state != null && state.equals(JSON_CLASS_ONLINE)) {
            return OnlineState.fromJSON(plant, json);
        }
        return OfflineState.fromJSON(plant, json);
    }

}
