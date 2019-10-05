package com.yorubadev.arny.ui.main;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.talkspaceapp.talkspace.R;
import com.talkspaceapp.talkspace.data.Repository;
import com.talkspaceapp.talkspace.data.database.ContactStatusItem;
import com.talkspaceapp.talkspace.utilities.Constants;
import com.talkspaceapp.talkspace.utilities.PreferenceUtils;
import com.talkspaceapp.talkspace.utilities.WorkUtils;

import java.util.LinkedHashMap;
import java.util.List;


class MainActivityViewModel extends ViewModel {
    private final Repository mRepository;
    private final Application mApplication;
    private final LinkedHashMap<String, LiveData<Integer>> contactsLiveDataMap = new LinkedHashMap<>();
    private LiveData<List<ContactStatusItem>> contactsList;
    private SharedPreferences.OnSharedPreferenceChangeListener chattingPrefListener;

    MainActivityViewModel(Application application, Repository repository) {
        mRepository = repository;
        mApplication = application;
//        mRepository.initContacts();
        initContacts();
        contactsList = mRepository.getContactStatusItems();
        fetchAndMonitorContactsStatus();
    }

    private void initContacts() {
        LiveData<Integer> contactsCountLiveData = mRepository.getContactsCount();
        contactsCountLiveData.observeForever(new Observer<Integer>() {
            @Override
            public void onChanged(Integer contactsCount) {
                if (contactsCount != null && contactsCount <= 0) {
                    WorkUtils.scheduleContactResolveWork(mApplication.getApplicationContext());
                    WorkUtils.schedulePeriodicContactResolveWork(mApplication.getApplicationContext());
                }
                contactsCountLiveData.removeObserver(this);
            }
        });
        WorkUtils.schedulePeriodicPresenceUpdateWork(mApplication.getApplicationContext());

        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mApplication.getApplicationContext());
        chattingPrefListener = (sharedPreferences1, key) -> {
            if (key.equals(mApplication.getApplicationContext().getString(R.string.pref_key_is_chatting)))
                liveData.setValue(PreferenceUtils.getChattingStatus(mApplication.getApplicationContext()));
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(chattingPrefListener);
        mRepository.trackUserStatus(liveData);
        liveData.setValue(PreferenceUtils.getChattingStatus(mApplication.getApplicationContext()));
    }

    String getUid() {
        return mRepository.getFirebaseUid();
    }

    private void fetchAndMonitorContactsStatus() {
        contactsList.observeForever(allContacts -> {
            if (allContacts != null) {
                for (ContactStatusItem contactStatusItem : allContacts) {
                    String contactId = contactStatusItem.getPushId();
                    if (contactsLiveDataMap.get(contactId) == null) {
                        if (contactStatusItem.getStatus() != Constants.CONTACT_STATUS_OFFLINE)
                            mRepository.updateStatus(contactId, Constants.CONTACT_STATUS_OFFLINE);
                        // This chat hasn't been observed before. Start observing
                        LiveData<Integer> contactStatusLiveData = mRepository.fetchStatus(contactId);
                        contactsLiveDataMap.put(contactId, contactStatusLiveData);
                        contactStatusLiveData.observeForever(status -> {
                            mRepository.updateStatus(contactId, status == null ? Constants.CONTACT_STATUS_OFFLINE : status);
                            if (status != null && status == Constants.CONTACT_STATUS_ONLINE) {
                                LiveData<Integer> missedRequestLiveData = mRepository.getTotalMissedRequestCount(contactId);
                                missedRequestLiveData.observeForever(new Observer<Integer>() {
                                    @Override
                                    public void onChanged(Integer missedRequestCount) {
                                        if (missedRequestCount != null && missedRequestCount > 0) {
                                            WorkUtils.scheduleMissedRequestNotificationWork(mApplication.getApplicationContext(), contactId);
                                        }
                                        missedRequestLiveData.removeObserver(this);
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }
}