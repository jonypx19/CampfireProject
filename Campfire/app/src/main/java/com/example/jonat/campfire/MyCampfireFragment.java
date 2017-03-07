package com.example.jonat.campfire;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import backend.algorithms.Comparable;
import backend.algorithms.ProgrammingLanguagesCriteria;
import backend.algorithms.Student;

public class MyCampfireFragment extends Fragment {

    private ArrayList<Student> sampleStudents;
    private ArrayList<String> c1;
    ArrayList<Comparable> crit;
    private ListView listView;

    private String uEmail;
    private Student uStudent;
    private String[] names;
    private Integer[] images;
    private String[] emails;
    private Integer sampleImage = R.drawable.person_icon;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        return inflater.inflate(R.layout.fragment_my_campfire, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        uStudent = ((MainActivity) getActivity()).getCurrentStudent();
        uEmail = uStudent.getEmail();

        c1 = new ArrayList<>();
        c1.add("Java");
        c1.add("Python");
        c1.add("HTML");
        c1.add("CSS");
        c1.add("JavaScript");

        ProgrammingLanguagesCriteria languages = new ProgrammingLanguagesCriteria(c1);

        crit = new ArrayList<>();
        crit.add(languages);

        sampleStudents = new ArrayList<>();
        sampleStudents.add(new Student("Adam", "Capparelli", "adam@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Andrew", "Goupil", "andrew@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Fullchee", "Zhang", "fullchee@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Jonathan", "Pelastine", "jonathan@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Quinn", "Daneyko", "quinn@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Rod", "Mazloomi", "rod@mail.com", "12345678", crit));
        sampleStudents.add(new Student("Vlad", "Chapurny", "vlad@mail.com", "12345678", crit));
        names = new String[sampleStudents.size()];
        images = new Integer[sampleStudents.size()];
        emails = new String[sampleStudents.size()];
        int i = 0;
        for (Student s : sampleStudents) {
            names[i] = s.getFname() + " " + s.getLname();
            emails[i] = s.getEmail();
            images[i] = sampleImage;
            i++;
        }
        MyCampfireList customList = new MyCampfireList(getActivity(), names, emails, images);

        listView = (ListView) getView().findViewById(R.id.listOfTeam);
        listView.setAdapter(customList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(names[i])
                        .setMessage(emails[i])
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNeutralButton("Message", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Fragment fragment = new MessagesFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("userEmail", uEmail);
                                fragment.setArguments(bundle);
                                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                                ft.replace(R.id.content_frame, fragment);
                                ft.commit();
                            }
                        })
                        .setIcon(images[i])
                        .show();
            }
        });
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("My Campfire");
    }
}
