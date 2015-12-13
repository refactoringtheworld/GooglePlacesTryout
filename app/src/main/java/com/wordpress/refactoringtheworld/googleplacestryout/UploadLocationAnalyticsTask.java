package com.wordpress.refactoringtheworld.googleplacestryout;

import android.content.Context;
import android.os.AsyncTask;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageCredentials;
import com.microsoft.azure.storage.StorageCredentialsAccountAndKey;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

import java.net.URISyntaxException;

/**
 * Created by Alb_Erc on 12/12/2015.
 */
public class UploadLocationAnalyticsTask extends AsyncTask<LocationAnalyticsEntity, Integer, Long>{
    private CloudStorageAccount storageAccount;
    private Context app;

    public UploadLocationAnalyticsTask(Context app) {
        try {
            this.app = app;

            String storageAccountName = this.app.getResources().getString(R.string.storage_account_name);
            String storageAccountKey = this.app.getResources().getString(R.string.storage_account_key);
            StorageCredentials credentials = new StorageCredentialsAccountAndKey(storageAccountName, storageAccountKey);

            this.storageAccount = new CloudStorageAccount(credentials);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Long doInBackground(LocationAnalyticsEntity... params) {
        CloudTableClient tableClient = storageAccount.createCloudTableClient();
        try {
            CloudTable table = tableClient.getTableReference(this.app.getResources().getString(R.string.storage_table_name));

            for (LocationAnalyticsEntity entity : params) {
                TableOperation operation = TableOperation.insert(entity);
                table.execute(operation);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (StorageException e) {
            e.printStackTrace();
        }

        return null;
    }
}
