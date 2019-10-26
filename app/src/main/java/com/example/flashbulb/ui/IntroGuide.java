package com.example.flashbulb.ui;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import com.example.flashbulb.R;
import com.example.flashbulb.adapter.IntroViewPagerAdapter;
import com.example.flashbulb.model.ScreenItem;
import java.util.ArrayList;
import java.util.List;
public class IntroGuide extends AppCompatActivity {
    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext,btnStart;
    int position = 0;
    Animation btnAnim;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_guide);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //buat isi intro
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Beranda",
                "Selamat datang di FlashBulb !! Ini adalah tampilan awal aplikasi yang akan menampilkan semua posting pengguna, Anda dapat melihat posting pengguna, membuat posting dan melihat profil Anda sendiri.",
                R.drawable.intro1));
        mList.add(new ScreenItem("Profil",
                "Jika Anda mengklik tombol profil gambar di bagian atas layar beranda, Anda dapat melihat data profil Anda, Anda dapat mengklik logout untuk menghapus akun dan Anda dapat melihat posting Anda jika Anda mengklik nomor posting Anda",
                R.drawable.intro2));
        mList.add(new ScreenItem("Posting Profil",
                "Di sini Anda dapat menghapus posting Anda sendiri dengan mengklik tombol X di sudut kanan",
                R.drawable.intro3));
        mList.add(new ScreenItem("Tombol Menu",
                "Dalam tampilan tombol menu ini, Anda dapat menggunakan tiga tombol, yang biru untuk Info, merah untuk Creat Post dan kuning untuk melihat peringkat 5 posting teratas.",
                R.drawable.intro4));
        mList.add(new ScreenItem("Buat Posting",
                "Jika Anda mengklik tombol biru maka Anda dapat membuat posting Anda, isi persyaratan dan klik gambar untuk memilih, maka Anda dapat mengklik tombol di tengah untuk membuat posting Anda",
                R.drawable.intro5));
        mList.add(new ScreenItem("Detail Post",
                "Jika Anda mengklik posting mana saja, tampilan akan pergi ke bagian ini untuk melihat detail posting, Anda dapat mengklik bintang untuk memberi peringkat posting, atau Anda dapat mengklik profil gambar untuk melihat profil pengguna lain, dan Anda dapat mengomentari posting ini",
                R.drawable.intro6));
        mList.add(new ScreenItem("peringkat",
                "Anda dapat memberikan skor atau peringkat dengan menggeser bintang-bintang atau klik saja",
                R.drawable.intro7));
        mList.add(new ScreenItem("Terakhir",
                "Semoga Anda menjadi fotografer profesional",
                R.drawable.thankyou));
        //setting ViewPager
        screenPager = findViewById(R.id.screenViewPager);
        tabIndicator = findViewById(R.id.tabIndicator);
        btnNext = findViewById(R.id.btn_next);
        btnStart = findViewById(R.id.getStart);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);
        introViewPagerAdapter = new IntroViewPagerAdapter(this,mList);
        screenPager.setAdapter(introViewPagerAdapter);
        //setting tablayout dengan ViewPager
        tabIndicator.setupWithViewPager(screenPager);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                position = screenPager.getCurrentItem();
                if(position <mList.size()){
                    position ++;
                    screenPager.setCurrentItem(position);
                }
                if(position == mList.size()-1){ //ketika slide akhir
                    loadLastScreen();
                }
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent home = new Intent(getApplicationContext(),Home.class);
                startActivity(home);
                finish();
            }
        });
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size()-1){
                    loadLastScreen();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnStart.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        btnStart.setAnimation(btnAnim);
    }
}
