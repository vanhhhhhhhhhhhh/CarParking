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
import com.example.carparking.api.ApiClient;
import com.example.carparking.api.ParkingApiService;
import com.example.carparking.model.Parking;
import com.example.carparking.util.SharedPrefManager;

import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ParkingRequestAdapter extends RecyclerView.Adapter<ParkingRequestAdapter.ParkingViewHolder> {

    private Context context;
    private List<Parking> parkingList;

    public ParkingRequestAdapter(Context context, List<Parking> parkingList) {
        this.context = context;
        this.parkingList = parkingList;
    }

    @NonNull
    @Override
    public ParkingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parking_request, parent, false);
        return new ParkingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkingViewHolder holder, int position) {
        Parking parking = parkingList.get(position);

        holder.tvParkingName.setText("Tên bãi: " + parking.getName());
        holder.tvParkingAddress.setText("Địa chỉ: " + parking.getAddress());
        holder.tvTotalSlots.setText("Tổng số chỗ: " + parking.getTotalSlots());
        holder.tvPricePerHour.setText("Giá theo giờ: " + String.format("%,d VND", parking.getPricePerHour()));
        holder.tvOwnerId.setText("Người gửi: " +
                (parking.getOwner() != null ? parking.getOwner().getFullName() : "Không xác định"));

        String status = parking.getStatus();
        holder.tvStatus.setText("Trạng thái: " + getStatusText(status));

        if ("approved".equalsIgnoreCase(status) || "rejected".equalsIgnoreCase(status)) {
            holder.btnApprove.setVisibility(View.GONE);
            holder.btnReject.setVisibility(View.GONE);
        } else {
            holder.btnApprove.setVisibility(View.VISIBLE);
            holder.btnReject.setVisibility(View.VISIBLE);

            holder.btnApprove.setOnClickListener(v -> updateRequestStatus(parking.getId(), "approved", position));
            holder.btnReject.setOnClickListener(v -> updateRequestStatus(parking.getId(), "rejected", position));
        }
    }

    @Override
    public int getItemCount() {
        return parkingList.size();
    }

    public static class ParkingViewHolder extends RecyclerView.ViewHolder {
        TextView tvParkingName, tvParkingAddress, tvTotalSlots, tvPricePerHour, tvOwnerId, tvStatus;
        Button btnApprove, btnReject;

        public ParkingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParkingName = itemView.findViewById(R.id.tvParkingName);
            tvParkingAddress = itemView.findViewById(R.id.tvParkingAddress);
            tvTotalSlots = itemView.findViewById(R.id.tvTotalSlots);
            tvPricePerHour = itemView.findViewById(R.id.tvPricePerHour);
            tvOwnerId = itemView.findViewById(R.id.tvOwnerId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnReject = itemView.findViewById(R.id.btnReject);
        }
    }

    private void updateRequestStatus(String parkingId, String status, int position) {
        String token = SharedPrefManager.getInstance(context).getToken();
        Retrofit retrofit = ApiClient.getClient(token);
        ParkingApiService apiService = retrofit.create(ParkingApiService.class);

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("application/json"),
                "{\"status\":\"" + status + "\"}"
        );

        Call<ResponseBody> call = apiService.manageRequest(parkingId, requestBody);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Yêu cầu đã được " + (status.equals("approved") ? "duyệt" : "từ chối"), Toast.LENGTH_SHORT).show();
                    parkingList.get(position).setStatus(status);
                    notifyItemChanged(position);
                } else {
                    Toast.makeText(context, "Không thể xử lý: trạng thái không hợp lệ hoặc đã xử lý", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "approved": return "Đã duyệt";
            case "rejected": return "Đã từ chối";
            case "pending": return "Chờ xử lý";
            default: return "Không xác định";
        }
    }
}
