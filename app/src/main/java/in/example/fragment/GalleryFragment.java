package in.example.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import in.rentifi.app.FullGalleryActivity;
import in.rentifi.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    ViewPager mViewPager;
    public static ArrayList<String> mList;
    CustomViewPagerAdapter mAdapter;

    public static GalleryFragment newInstance(ArrayList<String> categoryId) {
        GalleryFragment f = new GalleryFragment();
        mList = categoryId;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        mViewPager = rootView.findViewById(R.id.viewPager);
        mAdapter = new CustomViewPagerAdapter();
        mViewPager.setAdapter(mAdapter);
        return rootView;
    }

    private class CustomViewPagerAdapter extends PagerAdapter {
        private LayoutInflater inflater;

        public CustomViewPagerAdapter() {
            // TODO Auto-generated constructor stub
            inflater = getActivity().getLayoutInflater();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View imageLayout = inflater.inflate(R.layout.row_gallery_item, container, false);
            assert imageLayout != null;
            ImageView image = imageLayout.findViewById(R.id.image);
            TextView text = imageLayout.findViewById(R.id.textNumber);

            text.setText(position + 1 + "/" + mList.size());
            Picasso.get().load(mList.get(position)).placeholder(R.drawable.header_top_logo).into(image);
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(), FullGalleryActivity.class);
                    intent.putExtra("Position",position);
                    startActivity(intent);
                }
            });

            container.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }
    }
}
