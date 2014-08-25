package kr.tento.activity.fragement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import kr.tento.R;


public class MoodFragment extends Fragment implements View.OnTouchListener{


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View moodView = inflater.inflate(R.layout.fragment_mood, container, false);

        ImageView iView = (ImageView) moodView.findViewById(R.id.touchImageView);
        iView.setOnTouchListener(this);

        return moodView;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN ){

            float x = event.getX();
            float y = event.getY();

            String msg = "터치를 입력받음 : " +x+" / " +y;

            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
