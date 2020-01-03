package com.abclinic.server.service;

import com.google.api.gax.rpc.ApiException;
import com.google.common.collect.ImmutableList;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.internal.InternalPhotosLibraryClient;
import com.google.photos.library.v1.proto.*;
import com.google.photos.library.v1.upload.UploadMediaItemRequest;
import com.google.photos.library.v1.upload.UploadMediaItemResponse;
import com.google.photos.library.v1.util.NewMediaItemFactory;
import com.google.photos.types.proto.Album;
import com.google.photos.types.proto.MediaItem;
import com.google.rpc.Code;
import com.google.rpc.Status;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class GooglePhotosService {
    //TODO: Adjust the path according to yours
    private static final String CREDENTIAL_PATH = "C:\\Users\\ADMIN\\Desktop\\google-photos\\client_secret.json";
    private static final List<String> REQUIRED_SCOPES =
            ImmutableList.of(
                    "https://www.googleapis.com/auth/photoslibrary.readonly.appcreateddata",
                    "https://www.googleapis.com/auth/photoslibrary.appendonly");
    private static PhotosLibraryClient client;

    static {
        try {
            client = PhotosLibraryClientFactory.createClient(CREDENTIAL_PATH, REQUIRED_SCOPES);
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
    };

    public static Album makeAlbum() {
        return makeAlbum("album-" + LocalDateTime.now().toString());
    }

    public static Album makeAlbum(String albumName) {
        return client.createAlbum(albumName);
    }

    public static Album getAlbumById(String id) {
        return client.getAlbum(id);
    }

    public static Optional<Album> getAlbumByName(String name) {
        InternalPhotosLibraryClient.ListAlbumsPagedResponse response = client.listAlbums();
        return StreamSupport.stream(response.iterateAll().spliterator(), false)
                .filter(a -> a.getTitle().equals(name))
                .findFirst();
    }

    public static List<String> getAlbumImages(String id) {
        InternalPhotosLibraryClient.SearchMediaItemsPagedResponse response = client.searchMediaItems(id);
        return StreamSupport.stream(response.iterateAll().spliterator(), false)
                .map(MediaItem::getBaseUrl)
                .collect(Collectors.toList());
    }

    public static String getImage(String id) {
        MediaItem item = client.getMediaItem(id);
        return item.getBaseUrl();
    }

    public static String getToken(String mediaFileName, String pathToFile) {
        try {
            // Create a new upload request
            // Specify the filename that will be shown to the user in Google Photos
            // and the path to the file that will be uploaded
            UploadMediaItemRequest uploadRequest =
                    UploadMediaItemRequest.newBuilder()
                            //filename of the media item along with the file extension
                            .setFileName(mediaFileName)
                            .setDataFile(new RandomAccessFile(pathToFile, "r"))
                            .build();
            // Upload and capture the response
            UploadMediaItemResponse uploadResponse = client.uploadMediaItem(uploadRequest);
            if (uploadResponse.getError().isPresent()) {
                // If the upload results in an error, handle it
                UploadMediaItemResponse.Error error = uploadResponse.getError().get();
            } else {
                // If the upload is successful, get the uploadToken
                // Use this upload token to create a media item
                return uploadResponse.getUploadToken().get();
            }
        } catch (ApiException e) {
            // Handle error
        } catch (FileNotFoundException e) {
            // Local file could not be found for upload
        }
        return null;
    }

    public static String createItem(String uploadToken, String albumId, String itemDescription) {
        try {
            // Create a NewMediaItem with the uploadToken obtained from the previous upload request, and a description
            NewMediaItem newMediaItem = NewMediaItemFactory
                    .createNewMediaItem(uploadToken, itemDescription);
            List<NewMediaItem> newItems = Arrays.asList(newMediaItem);

            BatchCreateMediaItemsResponse response = client.batchCreateMediaItems(albumId, newItems);
            for (NewMediaItemResult itemsResponse : response.getNewMediaItemResultsList()) {
                Status status = itemsResponse.getStatus();
                if (status.getCode() == Code.OK_VALUE) {
                    // The item is successfully created in the user's library
                    MediaItem createdItem = itemsResponse.getMediaItem();
                    return createdItem.getId();
                } else {
                    // The item could not be created. Check the status and try again
                }
            }
        } catch (ApiException e) {
            // Handle error
        }
        return null;
    }
}
