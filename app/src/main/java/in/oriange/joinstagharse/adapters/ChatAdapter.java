package in.oriange.joinstagharse.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import in.oriange.joinstagharse.R;
import in.oriange.joinstagharse.activities.FullScreenImagesActivity;
import in.oriange.joinstagharse.models.ChatModel;
import in.oriange.joinstagharse.utilities.ApplicationConstants;
import in.oriange.joinstagharse.utilities.DownloadFile;
import in.oriange.joinstagharse.utilities.UserSessionManager;

import static in.oriange.joinstagharse.utilities.ApplicationConstants.IMAGE_LINK;
import static in.oriange.joinstagharse.utilities.Utilities.changeDateFormat;
import static in.oriange.joinstagharse.utilities.Utilities.openFile;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private Context context;
    private List<ChatModel.ResultBean> chatList;
    private String userId;
    private File chatFileFolder;


    public ChatAdapter(Context context, List<ChatModel.ResultBean> chatList) {
        this.context = context;
        this.chatList = chatList;

        UserSessionManager session = new UserSessionManager(context);
        try {
            JSONArray user_info = new JSONArray(session.getUserDetails().get(
                    ApplicationConstants.KEY_LOGIN_INFO));
            JSONObject json = user_info.getJSONObject(0);

            userId = json.getString("userid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        chatFileFolder = new File(Environment.getExternalStorageDirectory() + "/Joinsta Gharse/" + "Chat");
        if (!chatFileFolder.exists())
            chatFileFolder.mkdirs();

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_row_chat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int pos) {
        int position = holder.getAdapterPosition();
        ChatModel.ResultBean chatDetails = chatList.get(position);

        if (chatDetails.getSend_by().equals(userId)) {                          // OUTGOING MESSAGE
            holder.ll_outgoing_layout_bubble.setVisibility(View.VISIBLE);
            holder.ll_incoming_layout_bubble.setVisibility(View.GONE);

            switch (chatDetails.getChat_doc_type()) {
                case "0":
                    holder.imv_outgoing_image.setVisibility(View.GONE);
                    holder.ll_outgoing_doc.setVisibility(View.GONE);
                    break;
                case "1":
                    holder.imv_outgoing_image.setVisibility(View.VISIBLE);
                    holder.ll_outgoing_doc.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(IMAGE_LINK + "chat/" + chatDetails.getChat_image())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.imv_outgoing_image.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.imv_outgoing_image);
                    break;
                case "2":
                    holder.imv_outgoing_image.setVisibility(View.GONE);
                    holder.ll_outgoing_doc.setVisibility(View.VISIBLE);
                    holder.tv_outgoing_doc_name.setText(chatDetails.getChat_image());
                    break;
            }

            if (!chatDetails.getChat_text().equals(""))
                holder.tv_outgoing_text.setText(chatDetails.getChat_text());
            else
                holder.tv_outgoing_text.setVisibility(View.GONE);

            holder.tv_outgoing_date_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd/MM/yy hh:mma", chatDetails.getCreated_at()));

            holder.imv_outgoing_image.setOnClickListener(v -> {
                ArrayList<String> imageUrlList = new ArrayList<>();
                imageUrlList.add(IMAGE_LINK + "chat/" + chatDetails.getChat_image());
                context.startActivity(new Intent(context, FullScreenImagesActivity.class)
                        .putExtra("imageUrlList", imageUrlList)
                        .putExtra("position", 0));
            });

            holder.ll_outgoing_doc.setOnClickListener(v -> {
                File downloadedFile = new File(chatFileFolder.toString() + "/" + chatDetails.getChat_image());
                if (downloadedFile.isFile()) {
                    openFile(context, downloadedFile.toString(), downloadedFile);
                } else {
                    new DownloadFile(context, "Chat", IMAGE_LINK + "chat/" + chatDetails.getChat_image());
                }
            });

        } else {                                                                // INCOMING MESSAGE
            holder.ll_outgoing_layout_bubble.setVisibility(View.GONE);
            holder.ll_incoming_layout_bubble.setVisibility(View.VISIBLE);

            switch (chatDetails.getChat_doc_type()) {
                case "0":
                    holder.imv_incoming_image.setVisibility(View.GONE);
                    holder.ll_incoming_doc.setVisibility(View.GONE);
                    break;
                case "1":
                    holder.imv_incoming_image.setVisibility(View.VISIBLE);
                    holder.ll_incoming_doc.setVisibility(View.GONE);
                    Glide.with(context)
                            .load(IMAGE_LINK + "chat/" + chatDetails.getChat_image())
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    holder.imv_incoming_image.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            })
                            .into(holder.imv_incoming_image);

                    break;
                case "2":
                    holder.imv_incoming_image.setVisibility(View.GONE);
                    holder.ll_incoming_doc.setVisibility(View.VISIBLE);
                    holder.tv_incoming_doc_name.setText(chatDetails.getChat_image());
                    break;
            }

            if (!chatDetails.getChat_text().equals(""))
                holder.tv_incoming_text.setText(chatDetails.getChat_text());
            else
                holder.tv_incoming_text.setVisibility(View.GONE);

            holder.tv_incoming_date_time.setText(changeDateFormat("yyyy-MM-dd HH:mm:ss", "dd/MM/yy hh:mma", chatDetails.getCreated_at()));

            holder.imv_incoming_image.setOnClickListener(v -> {
                ArrayList<String> imageUrlList = new ArrayList<>();
                imageUrlList.add(IMAGE_LINK + "chat/" + chatDetails.getChat_image());
                context.startActivity(new Intent(context, FullScreenImagesActivity.class)
                        .putExtra("imageUrlList", imageUrlList)
                        .putExtra("position", 0));
            });

            holder.ll_incoming_doc.setOnClickListener(v -> {
                File downloadedFile = new File(chatFileFolder.toString() + "/" + chatDetails.getChat_image());
                if (downloadedFile.isFile()) {
                    openFile(context, downloadedFile.toString(), downloadedFile);
                } else {
                    new DownloadFile(context, "Chat", IMAGE_LINK + "chat/" + chatDetails.getChat_image());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout ll_incoming_layout_bubble, ll_outgoing_layout_bubble, ll_incoming_doc, ll_outgoing_doc;
        private ImageView imv_incoming_image, imv_outgoing_image;
        private TextView tv_incoming_text, tv_outgoing_text, tv_incoming_doc_name, tv_outgoing_doc_name,
                tv_incoming_date_time, tv_outgoing_date_time;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_incoming_layout_bubble = itemView.findViewById(R.id.ll_incoming_layout_bubble);
            ll_outgoing_layout_bubble = itemView.findViewById(R.id.ll_outgoing_layout_bubble);
            ll_incoming_doc = itemView.findViewById(R.id.ll_incoming_doc);
            ll_outgoing_doc = itemView.findViewById(R.id.ll_outgoing_doc);
            imv_incoming_image = itemView.findViewById(R.id.imv_incoming_image);
            imv_outgoing_image = itemView.findViewById(R.id.imv_outgoing_image);
            tv_incoming_text = itemView.findViewById(R.id.tv_incoming_text);
            tv_outgoing_text = itemView.findViewById(R.id.tv_outgoing_text);
            tv_incoming_doc_name = itemView.findViewById(R.id.tv_incoming_doc_name);
            tv_outgoing_doc_name = itemView.findViewById(R.id.tv_outgoing_doc_name);
            tv_incoming_date_time = itemView.findViewById(R.id.tv_incoming_date_time);
            tv_outgoing_date_time = itemView.findViewById(R.id.tv_outgoing_date_time);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
