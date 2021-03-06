package com.uoft.jonathan.campfire;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import backend.algorithms.Comparable;
import backend.algorithms.Course;
import backend.algorithms.Student;
import backend.database.DbAdapter;

import static android.content.Context.MODE_PRIVATE;


/**
 * Created by jonat on 25-Feb-2017.
 */

//Used https://blog.mindorks.com/android-tinder-swipe-view-example-3eca9b0d4794#.lywnigtq7 quite
//heavily.
public class HomeFragment extends Fragment {

    private String[] newStudentID;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    private Student uStudent;
    private List<Student> allStudents;
    private ArrayList<Student> otherStudents301;
    private ArrayList<Student> otherStudents;
    private ArrayList<Student> students301;
    private List<Student> sortedStudents;
    private ArrayList<String> c1;
    private ArrayList<String> c2;
    public static List<Student> loadedStudents = new ArrayList<Student>();
    private MaterialDialog loadingUsersDialog;
    ArrayList<Comparable> crit;
    Course csc301;
    Course currentCourse;
    String currentCourseCode;
    private String uEmail;
    private List<Student> matchedAlready;

    SharedPreferences prefs = null;

    //TODO: Make this work with database, instead of local.
    public Map<String, List<Student>> swipedRight = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        loadedStudents = new ArrayList<>();
        newStudentID = getArguments().getStringArray("identity");
        currentCourseCode = getArguments().getString("currentCourse");
        prefs = getContext().getSharedPreferences("come.example.jonat.campfire", MODE_PRIVATE);
        uEmail = newStudentID[2];
        matchedAlready = DbAdapter.getMatchedMap(uEmail).get(currentCourseCode);
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Home");
        loadCards();
   }

   public void loadCards(){
       MainActivity main = (MainActivity) getActivity();

       uStudent = main.getCurrentStudent();
       currentCourse = main.getCurrentCourse();

       students301 = uStudent.getallOtherCourseStudents(currentCourse);

       uStudent = ((MainActivity) getActivity()).getCurrentStudent();
       //TODO: Does not work, includes current student.
//       otherStudents301 = uStudent.getallOtherCourseStudents(currentCourse);
       otherStudents = uStudent.getallOtherCourseStudents(currentCourse);
       uStudent.MatchWithClass(currentCourse, true);
       sortedStudents = uStudent.validSortedStudents(currentCourse);
       mSwipeView = (SwipePlaceHolderView) getActivity().findViewById(R.id.swipeView);

       mContext = getActivity().getApplicationContext();
       //TODO: Probably not the best way of doing margin adjustments
       int bottomMargin = Utils.dpToPx(180);
       int sideMargin = Utils.dpToPx(32);
       Point windowSize = Utils.getDisplaySize(getActivity().getWindowManager());
       mSwipeView.getBuilder()
               .setDisplayViewCount(3)
               .setHeightSwipeDistFactor(10)
               .setWidthSwipeDistFactor(5)
               .setSwipeDecor(new SwipeDecor()
                       .setViewWidth(windowSize.x - sideMargin)
                       .setViewHeight(windowSize.y - bottomMargin)
                       .setViewGravity(Gravity.TOP)
                       .setPaddingTop(20)
                       .setRelativeScale(0.01f)
                       .setSwipeInMsgLayoutId(R.layout.tinder_swipe_in_msg_view)
                       .setSwipeOutMsgLayoutId(R.layout.tinder_swipe_out_msg_view));

       // Change swipeOn to:
       //     - allStudents for every student in db.
       //     - otherStudents301 for all other students in csc301h1. (Currently unavailable)
       //     - students301 for all students in csc301h1.

       List<Student> swipeOn = sortedStudents;
       if ((swipeOn.isEmpty() && loadedStudents.isEmpty())) {
           System.out.println("empty");
           Toast.makeText(getActivity(), "You've Swiped Right on Everyone! All you can do now is wait!",
                   Toast.LENGTH_LONG).show();
       }
       else if (matchedAlready != null) {
           if (swipeOn.size() == matchedAlready.size()) {
               System.out.println("same size");
               Toast.makeText(getActivity(), "You've Swiped Right on Everyone! All you can do now is wait!",
                       Toast.LENGTH_LONG).show();
           }
           else {
               System.out.println("else");
               System.out.println(swipeOn);
               for (Student s : swipeOn) {
                   System.out.println(s.getFname());
                   System.out.println(inCampfire(s) + " " + swipedYet(s, currentCourseCode) + " " + uStudent.getEmail().equals(s.getEmail()));
                   if (!inCampfire(s) && !swipedYet(s, currentCourseCode) && !uStudent.getEmail().equals(s.getEmail())) {
                       mSwipeView.addView(new TinderCard(getContext(), mSwipeView, s, uStudent, currentCourse, getActivity(), this));
                   }
               }
           }
       }
       else {
           System.out.println("else");
           System.out.println(swipeOn);
           for (Student s : swipeOn) {
               System.out.println(s.getFname());
               System.out.println(inCampfire(s) + " " + swipedYet(s, currentCourseCode) + " " + uStudent.getEmail().equals(s.getEmail()));
               if (!inCampfire(s) && !swipedYet(s, currentCourseCode) && !uStudent.getEmail().equals(s.getEmail())) {
                   mSwipeView.addView(new TinderCard(getContext(), mSwipeView, s, uStudent, currentCourse, getActivity(), this));
               }
           }
       }

       getActivity().findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mSwipeView.doSwipe(false);
           }
       });

       getActivity().findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               mSwipeView.doSwipe(true);
           }
       });

       getActivity().findViewById(R.id.infoBtn).setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (loadedStudents != null && loadedStudents.size() > 0) {
                   Intent profileIntent = new Intent(getActivity(), ClassmatesProfileActivity.class);
                   profileIntent.putExtra("studentEmail", loadedStudents.get(0).getEmail());
                   startActivity(profileIntent);
               }
           }
       });

       if (prefs.getBoolean("firstrun", true)) {
           new AlertDialog.Builder(getActivity())
                   .setTitle("Welcome to Campfire!")
                   .setMessage("Get ready to start building a great team!")
                   .setNeutralButton("Join the Campfire", new DialogInterface.OnClickListener() {
                       public void onClick(DialogInterface dialog, int which) {
                       }
                   })
                   .show();
           prefs.edit().putBoolean("firstrun", false).commit();
       }
   }

    //Temporary helper for finding if student is in campfire.
    private boolean inCampfire(Student s) {
        for (Student stu : MyCampfireFragment.campfireStudents) {
            if (stu.getEmail().equals(s.getEmail())) {
                return true;
            }
        }
        return false;
    }

    //Helper for checking if swiped right yet.
    private boolean swipedYet(Student s, String c) {
        if (swipedRight != null) {
            if (swipedRight.get(c) != null) {
                for (Student stu : swipedRight.get(c)) {
                    if (stu.getEmail().equals(s.getEmail())) {
                        System.out.println("In SwipedRight");
                        return true;
                    }
                }
            }
        }
        if (matchedAlready != null) {
            for (Student stu : matchedAlready) {
                if (stu.getEmail().equals(s.getEmail())) {
                    System.out.println("In matched");
                    return true;
                }
            }
        }
        return false;
    }

    public void addToSwiped(Student s, String c) {
        if (swipedRight.containsKey(c)) {
            swipedRight.get(c).add(s);
        }
        else {
            List<Student> ls = new LinkedList<>();
            ls.add(s);
            swipedRight.put(c, ls);
        }
    }
}
