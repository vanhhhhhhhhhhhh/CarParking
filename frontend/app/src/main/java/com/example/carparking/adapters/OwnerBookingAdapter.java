package com.example.carparking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carparking.R;
import com.example.carparking.api.OwnerBookingApiService;
import com.example.carparking.model.BookingListing;
import com.example.carparking.model.BookingStatus;
import com.example.carparking.model.ResponseWrapper;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OwnerBookingAdapter extends RecyclerView.Adapter<OwnerBookingAdapter.ViewHolder> {

    private final List<BookingListing> bookings;
    private final OwnerBookingApiService api;
    private final Runnable reloadCallback;
    private final Context context;

    public OwnerBookingAdapter(Context context, List<BookingListing> bookings, OwnerBookingApiService api, Runnable reloadCallback) {
        this.context = context;
        this.bookings = bookings;
        this.api = api;
        this.reloadCallback = reloadCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_request, parent, false);
        return new ViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingListing b = bookings.get(position);
        holder.tvUserName.setText("Người dùng: " + b.getUserName());
        holder.tvParkingName.setText("Bãi đỗ: " + b.getParkingName());
        holder.tvVehicleNumber.setText("Biển số xe: " + b.getVehicleNumber());
        holder.tvTimeRange.setText("Từ: " + b.getStartTime() + " đến " + b.getEndTime());
        holder.tvTotalPrice.setText("Tổng tiền: " + b.getTotalPrice() + " VND");
        holder.tvBookingStatus.setText("Trạng thái: " + b.getStatusText());

        boolean isPending = b.getStatus() == BookingStatus.PENDING;
        if (isPending) {
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
        } else {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
        }

        holder.btnConfirm.setOnClickListener(v -> {
            api.confirmBooking(b.getId()).enqueue(new Callback<ResponseWrapper<Object>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<Object>> call, Response<ResponseWrapper<Object>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        b.setStatus(BookingStatus.CONFIRMED);
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(context, "Đã xác nhận đơn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseWrapper<Object>> call, Throwable t) {
                    Toast.makeText(context, "Lỗi xác nhận: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.btnCancel.setOnClickListener(v -> {
            api.cancelBooking(b.getId()).enqueue(new Callback<ResponseWrapper<Object>>() {
                @Override
                public void onResponse(Call<ResponseWrapper<Object>> call, Response<ResponseWrapper<Object>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        b.setStatus(BookingStatus.CANCELLED);
                        notifyItemChanged(holder.getAdapterPosition());
                        Toast.makeText(context, "Đã hủy đơn", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseWrapper<Object>> call, Throwable t) {
                    Toast.makeText(context, "Lỗi hủy đơn: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    private Callback<ResponseWrapper<Object>> callback() {
        return new Callback<ResponseWrapper<Object>>() {
            @Override
            public void onResponse(Call<ResponseWrapper<Object>> call, Response<ResponseWrapper<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    reloadCallback.run();
                }
            }

            @Override
            public void onFailure(Call<ResponseWrapper<Object>> call, Throwable t) {}
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvParkingName, tvVehicleNumber, tvTimeRange, tvTotalPrice, tvBookingStatus;
        Button btnConfirm, btnCancel;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvParkingName = itemView.findViewById(R.id.tvParkingName);
            tvVehicleNumber = itemView.findViewById(R.id.tvVehicleNumber);
            tvTimeRange = itemView.findViewById(R.id.tvTimeRange);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvBookingStatus = itemView.findViewById(R.id.tvBookingStatus);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
            btnCancel = itemView.findViewById(R.id.btnCancel);
        }
    }
}
