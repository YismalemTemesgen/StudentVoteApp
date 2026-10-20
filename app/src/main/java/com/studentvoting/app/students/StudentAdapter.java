package com.studentvoting.app.students;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.studentvoting.app.R;
import com.studentvoting.app.database.Student;

import java.util.ArrayList;
import java.util.List;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    public interface OnStudentClickListener {
        void onStudentClick(Student student);
    }

    private List<Student> students = new ArrayList<>();
    private final Context context;
    private OnStudentClickListener listener;

    private static final int[] AVATAR_COLORS = {
            0xFF3F51B5, 0xFF0288D1, 0xFF00897B, 0xFF43A047,
            0xFFE91E63, 0xFF8E24AA, 0xFFFF6F00, 0xFF6D4C41
    };

    public StudentAdapter(Context context) {
        this.context = context;
    }

    public void setOnStudentClickListener(OnStudentClickListener listener) {
        this.listener = listener;
    }

    public void setStudents(List<Student> students) {
        this.students = students != null ? students : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = students.get(position);
        holder.bind(student, position);
    }

    @Override
    public int getItemCount() {
        return students.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView tvInitial;
        private final TextView tvName;
        private final TextView tvStudentId;
        private final TextView tvDepartment;
        private final TextView tvYear;
        private final TextView tvVoteStatus;

        StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_student);
            tvInitial = itemView.findViewById(R.id.tv_initial);
            tvName = itemView.findViewById(R.id.tv_student_name);
            tvStudentId = itemView.findViewById(R.id.tv_student_id);
            tvDepartment = itemView.findViewById(R.id.tv_student_department);
            tvYear = itemView.findViewById(R.id.tv_student_year);
            tvVoteStatus = itemView.findViewById(R.id.tv_vote_status);
        }

        void bind(Student student, int position) {
            String initial = student.getName().substring(0, 1).toUpperCase();
            tvInitial.setText(initial);

            int colorIndex = position % AVATAR_COLORS.length;
            tvInitial.getBackground().setTint(AVATAR_COLORS[colorIndex]);

            tvName.setText(student.getName());
            tvStudentId.setText("ID: " + student.getStudentId());
            tvDepartment.setText(student.getDepartment());
            tvYear.setText(student.getYear());

            if (student.isHasVoted()) {
                tvVoteStatus.setText("Voted");
                tvVoteStatus.getBackground().setTint(0xFF4CAF50);
            } else {
                tvVoteStatus.setText("Not Voted");
                tvVoteStatus.getBackground().setTint(0xFFFF9800);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStudentClick(student);
                }
            });

            // Animate
            itemView.setAlpha(0f);
            itemView.setTranslationY(30f);
            itemView.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(position * 50L)
                    .start();
        }
    }
}
