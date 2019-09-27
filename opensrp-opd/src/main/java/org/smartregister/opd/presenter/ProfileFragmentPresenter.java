package org.smartregister.opd.presenter;


import org.jeasy.rules.api.Facts;
import org.smartregister.opd.contract.OpdProfileFragmentContract;
import org.smartregister.opd.interactor.ProfileFragmentInteractor;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 2019-09-27
 */
public class ProfileFragmentPresenter implements OpdProfileFragmentContract.Presenter {
    private static final String TAG = ProfileFragmentPresenter.class.getCanonicalName();

    private WeakReference<OpdProfileFragmentContract.View> mProfileView;
    private OpdProfileFragmentContract.Interactor mProfileInteractor;

    public ProfileFragmentPresenter(OpdProfileFragmentContract.View profileView) {
        mProfileView = new WeakReference<>(profileView);
        mProfileInteractor = new ProfileFragmentInteractor(this);
    }

    public void onDestroy(boolean isChangingConfiguration) {
        mProfileView = null;//set to null on destroy

        // Inform interactor
        if (mProfileInteractor != null) {
            mProfileInteractor.onDestroy(isChangingConfiguration);
        }

        // Activity destroyed set interactor to null
        if (! isChangingConfiguration) {
            mProfileInteractor = null;
        }
    }

    @Override
    public OpdProfileFragmentContract.View getProfileView() {
        if (mProfileView != null) {
            return mProfileView.get();
        } else {
            return null;
        }
    }

    @Override
    public Facts getImmediatePreviousContact(Map<String, String> clientDetails, String baseEntityId, String contactNo) {
        Facts facts = new Facts();
        /*try {
            facts = AncApplication.getInstance().getPreviousContactRepository()
                    .getPreviousContactFacts(baseEntityId, contactNo, true);

            Map<String, Object> factsAsMap = facts.asMap();
            String attentionFlags = "";
            if (factsAsMap.containsKey(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS)) {
                attentionFlags = (String) factsAsMap.get(Constants.DETAILS_KEY.ATTENTION_FLAG_FACTS);
            }

            if (! TextUtils.isEmpty(attentionFlags)) {
                JSONObject jsonObject = new JSONObject(attentionFlags);
                if (jsonObject.length() > 0) {
                    Iterator<String> keys = jsonObject.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        facts.put(key, jsonObject.get(key));
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, Log.getStackTraceString(e));
        }*/

        return facts;
    }
}