package edu.ecu.cs.pirateplaces;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Displays an individual Pirate Place so it can be edited
 *
 * @author Mark Hills (mhills@cs.ecu.edu)
 * @version 1.1
 */
public class PiratePlaceFragment extends Fragment
{
    /** The PiratePlace to edit */
    private PiratePlace mPiratePlace;

    /** The tag for the ID */
    private static final String ARG_PLACE_ID = "place_id";

    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_PHOTO = 2;

    private EditText mPlaceNameTextView;
    private EditText mLastVisitedTextView;
    private Button mResetDateButton;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mSendToFriendButton;
    private Callbacks mCallbacks;
    private RecyclerView mRecyclerView;
    private ImageAdapter mAdapter;


    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onPiratePlaceUpdated(PiratePlace piratePlace);
        void onPiratePlaceDeleted(PiratePlace piratePlace);
    }

    public static PiratePlaceFragment newInstance(UUID id)
    {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PLACE_ID, id);

        PiratePlaceFragment fragment = new PiratePlaceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }

    public static UUID getUpdatedId(Intent data)
    {
        return (UUID) data.getSerializableExtra(PiratePlaceFragment.ARG_PLACE_ID);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        UUID id = (UUID) getArguments().getSerializable(ARG_PLACE_ID);
        PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
        mPiratePlace = pirateBase.getPiratePlace(id);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_pirate_place, container, false);

        mPlaceNameTextView = (EditText) v.findViewById(R.id.pirate_place_name);
        mPlaceNameTextView.setText(mPiratePlace.getPlaceName());
        mPlaceNameTextView.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                mPiratePlace.setPlaceName(s.toString());
                updatePiratePlace();
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        mLastVisitedTextView = (EditText) v.findViewById(R.id.pirate_place_last_visited);
        mLastVisitedTextView.setKeyListener(null);
        updateLastVisitedDate();

        mResetDateButton = (Button) v.findViewById(R.id.check_in_button);
        mResetDateButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mPiratePlace.setLastVisited(new Date());
                updatePiratePlace();
                updateLastVisitedDate();
            }
        });

        mDateButton = (Button) v.findViewById(R.id.edit_date);
        mTimeButton = (Button) v.findViewById(R.id.edit_time);

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mPiratePlace.getLastVisited());
                dialog.setTargetFragment(PiratePlaceFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mPiratePlace.getLastVisited());
                dialog.setTargetFragment(PiratePlaceFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        mSendToFriendButton = (Button) v.findViewById(R.id.send_to_friend);
        mSendToFriendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent sendMessage =
                        new Intent(Intent.ACTION_SEND)
                        .setType("text/plain")
                        .putExtra(Intent.EXTRA_SUBJECT, getString(R.string.message_subject))
                        .putExtra(Intent.EXTRA_TEXT, getMessageText())
                        ;
                sendMessage = Intent.createChooser(sendMessage, getString(R.string.send_message_title));
                startActivity(sendMessage);
            }
        });

        mRecyclerView = (RecyclerView) v.findViewById(R.id.pirate_place_images_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        updateUI();

        return v;
    }

    protected void updateUI() {
        PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
        List<File> imageFiles = pirateBase.getPhotoFiles(mPiratePlace);

        if (mAdapter == null) {
            mAdapter = new ImageAdapter(imageFiles);
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.updateImageFiles(imageFiles);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE && data != null) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mPiratePlace.setLastVisited(date);
            updatePiratePlace();
            updateLastVisitedDate();
        } else if (requestCode == REQUEST_TIME && data != null) {
            Date date = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_DATE);
            mPiratePlace.setLastVisited(date);
            updatePiratePlace();
            updateLastVisitedDate();
        } else if (requestCode == REQUEST_PHOTO) {

//            getActivity().revokeUriPermission(uri,
//                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            updatePiratePlace();
            updateUI();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        PirateBase.getPirateBase(getActivity()).updatePiratePlace(mPiratePlace);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_pirate_place, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.delete_place:
                PirateBase.getPirateBase(getActivity()).deletePiratePlace(mPiratePlace);
                mCallbacks.onPiratePlaceDeleted(mPiratePlace);
                return true;
            case R.id.take_picture:
                PirateBase pirateBase = PirateBase.getPirateBase(getActivity());
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "edu.ecu.cs.pirateplaces.fileprovider",
                        pirateBase.getNewPhotoFile(mPiratePlace));

                final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImage, REQUEST_PHOTO);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateLastVisitedDate()
    {
        String lastVisitedDate = DateFormat.getDateFormat(getActivity()).format(mPiratePlace.getLastVisited());
        String lastVisitedTime = DateFormat.getTimeFormat(getActivity()).format(mPiratePlace.getLastVisited());
        mLastVisitedTextView.setText(lastVisitedDate + " " + lastVisitedTime);
    }

    private String getMessageText()
    {
        String dateFormat = "MMMM d, yyyy";
        String dateString = DateFormat.format(dateFormat, mPiratePlace.getLastVisited()).toString();

        String timeFormat = "K:mm a";
        String timeString = DateFormat.format(timeFormat, mPiratePlace.getLastVisited()).toString();

        String message = getString(R.string.message_text,
                mPiratePlace.getPlaceName(),
                dateString,
                timeString);

        return message;
    }

    private void updatePiratePlace()
    {
        PirateBase.getPirateBase(getActivity()).updatePiratePlace(mPiratePlace);
        mCallbacks.onPiratePlaceUpdated(mPiratePlace);
    }

    private class ImageHolder extends RecyclerView.ViewHolder
    {
        private ImageView mImageView;
        private Bitmap mImageBitmap;

        public ImageHolder(LayoutInflater inflater, ViewGroup parent)
        {
            super(inflater.inflate(R.layout.fragment_pirate_place_image, parent, false));
            mImageView = (ImageView) itemView.findViewById(R.id.item_image_view);
        }

        public void bind(Bitmap bitmap)
        {
            mImageBitmap = bitmap;
            mImageView.setImageBitmap(mImageBitmap);
        }
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageHolder>
    {
        List<File> mImageFiles;

        public ImageAdapter(List<File> imageFiles)
        {
            mImageFiles = imageFiles;
        }

        public void updateImageFiles(List<File> imageFiles)
        {
            mImageFiles = imageFiles;
        }

        @Override
        public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new ImageHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(ImageHolder holder, int position)
        {
            File photoFile = mImageFiles.get(position);
            Bitmap bitmap = null;
            if (! (photoFile == null || !photoFile.exists())) {
                bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
            }

            holder.bind(bitmap);
        }

        @Override
        public int getItemCount()
        {
            return mImageFiles.size();
        }
    }
}
