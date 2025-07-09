package com.example.carparking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.model.BookingListing;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private final List<BookingListing> bookings;
    private final Context context;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onViewDetailsClick(BookingListing booking);
    }

    public BookingAdapter(Context context, List<BookingListing> bookings) {
        this.context = context;
        this.bookings = bookings;
    }

    public void setOnBookingClickListener(OnBookingClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingListing booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder {
        private final CardView statusAvatar;
        private final ImageView statusIcon;
        private final TextView tvAddress;
        private final TextView tvPrice;
        private final TextView tvTime;
        private final TextView tvStatus;
        private final MaterialButton btnViewDetails;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            statusAvatar = itemView.findViewById(R.id.statusAvatar);
            statusIcon = itemView.findViewById(R.id.statusIcon);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
        }

        public void bind(BookingListing booking) {
            tvAddress.setText(booking.getAddress());
            tvPrice.setText(booking.getTotalPrice());
            tvTime.setText(booking.getStartTime() + " - " + booking.getEndTime());
            tvStatus.setText(booking.getStatus().toString());

            setStatusAppearance(booking.getStatus());

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewDetailsClick(booking);
                }
            });
        }

        private void setStatusAppearance(BookingListing.Status status) {
            int backgroundColor;
            int iconResource;
            int iconTint;

            switch (status) {
                case COMPLETED:
                    backgroundColor = ContextCompat.getColor(context, R.color.success_container);
                    iconResource = R.drawable.ic_check;
                    iconTint = ContextCompat.getColor(context, R.color.on_success_container);
                    break;
                case PENDING:
                    backgroundColor = ContextCompat.getColor(context, R.color.warning_container);
                    iconResource = R.drawable.ic_time;
                    iconTint = ContextCompat.getColor(context, R.color.on_warning_container);
                    break;
                case CONFIRMED:
                    backgroundColor = ContextCompat.getColor(context, R.color.info_container);
                    iconResource = R.drawable.ic_confirmed;
                    iconTint = ContextCompat.getColor(context, R.color.on_info_container);
                    break;
                case CANCELLED:
                    backgroundColor = ContextCompat.getColor(context, R.color.error_container);
                    iconResource = R.drawable.ic_cancel;
                    iconTint = ContextCompat.getColor(context, R.color.on_error_container);
                    break;
                default:
                    backgroundColor = ContextCompat.getColor(context, R.color.surface_variant);
                    iconResource = R.drawable.ic_time;
                    iconTint = ContextCompat.getColor(context, R.color.on_surface_variant);
                    break;
            }

            statusAvatar.setCardBackgroundColor(backgroundColor);
            statusIcon.setImageResource(iconResource);
            statusIcon.setColorFilter(iconTint);
        }
    }
} 