package com.jacekz.android.menelsztandar;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    EditText logIngEditText;
    EditText passwordEditText;
    TextView textViewLabel;
    Button loginButton,logoutButton,battleButton;
    final ArrayList<Menel> playerChar = new ArrayList<>();
    ConstraintLayout char1,char2,char3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logIngEditText = findViewById(R.id.editTextLogin);
        passwordEditText = findViewById(R.id.editTextPassword);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loginButton = findViewById(R.id.buttonLogin);
        textViewLabel = findViewById(R.id.labelCreateAccount);

        logoutButton = findViewById(R.id.buttonLogOut);
        battleButton = findViewById(R.id.buttonFight);
        char1 = findViewById(R.id.char1);
        char2 = findViewById(R.id.char2);
        char3 = findViewById(R.id.char3);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
            }
        };

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("CALLED", "OnActivity Result");
            if (resultCode == 1) {
                Menel char1 = (Menel) data.getSerializableExtra("char1");
                Menel char2 = (Menel) data.getSerializableExtra("char2");
                Menel char3 = (Menel) data.getSerializableExtra("char3");
                while (char1.getExp() > (Math.pow(char1.getLvl(),2)*100))
                {
                    char1.lvlup();
                }
                while (char2.getExp() > (Math.pow(char2.getLvl(),2)*100)) {
                    char2.lvlup();
                }
                while (char3.getExp() > (Math.pow(char3.getLvl(),2)*100)){
                    char3.lvlup();
                }
                playerChar.clear();
                playerChar.add(char1);
                playerChar.add(char2);
                playerChar.add(char3);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String name = user.getEmail();
                String[] actualValue = name.split("@");
                databaseReference = database.getReference(actualValue[0]);
                fillData();
                ArrayList<Menel> listMenel = new ArrayList<Menel>();
                listMenel.add(playerChar.get(0));
                listMenel.add(playerChar.get(1));
                listMenel.add(playerChar.get(2));
                databaseReference.setValue(listMenel);
            }
    }
//button functions
    public void registerAccount(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View dView = getLayoutInflater().inflate(R.layout.dialogloginlayout,null);
        final EditText email = dView.findViewById(R.id.email);
        final EditText password = dView.findViewById(R.id.password);
        final EditText checkPassword = dView.findViewById(R.id.correctpassword);
        final EditText user1 = dView.findViewById(R.id.charname1);
        final EditText user2 = dView.findViewById(R.id.charname2);
        final EditText user3 = dView.findViewById(R.id.charname3);
        Button cancel = dView.findViewById(R.id.cancel);
        Button createUser = dView.findViewById(R.id.create);
        createUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a =password.getText().toString() ;
                String b =checkPassword.getText().toString() ;
                if (!a.equals(b))
                {
                    Toast.makeText(MainActivity.this, R.string.ErrorPasswords,Toast.LENGTH_SHORT).show();
                }
                else if(!email.getText().toString().isEmpty() && !password.getText().toString().isEmpty() && !checkPassword.getText().toString().isEmpty() &&
                        !user1.getText().toString().isEmpty() && !user2.getText().toString().isEmpty() && !user3.getText().toString().isEmpty())
                {
                    //success
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "nie mozna utworzyc konta", Toast.LENGTH_SHORT).show();
                                        }else
                                    {
                                        Toast.makeText(MainActivity.this,"konto zostalo utworzone",Toast.LENGTH_SHORT).show();
                                        database = FirebaseDatabase.getInstance();
                                        String[] actualValue = email.getText().toString().split("@");
                                        databaseReference = database.getReference(actualValue[0]);
                                        Menel character1 = new Menel(user1.getText().toString());
                                        Menel character2 = new Menel(user2.getText().toString());
                                        Menel character3 = new Menel(user3.getText().toString());
                                        ArrayList<Menel> listMenel = new ArrayList<Menel>();
                                        listMenel.add(character1);
                                        listMenel.add(character2);
                                        listMenel.add(character3);
                                        databaseReference.setValue(listMenel);
                                        finish();

                                    }
                                }
                            });

                }
                else {
                    Toast.makeText(MainActivity.this, R.string.emptyField,Toast.LENGTH_SHORT).show();
                }

            }
        });
        builder.setView(dView);
        final AlertDialog dialog = builder.create();
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void LogIn(View view) {

        final String emailString = logIngEditText.getText().toString();
        final String passwordString = passwordEditText.getText().toString();
        if (!emailString.equals("") && !passwordString.equals(""))
        {
            mAuth.signInWithEmailAndPassword(emailString,passwordString)
                    .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(MainActivity.this,"Bledne dane logowania",Toast.LENGTH_SHORT).show();
                            else {
                                Toast.makeText(MainActivity.this, "Zalogowano", Toast.LENGTH_SHORT).show();

                                ShowData showData = new ShowData();
                                showData.execute(emailString.toString());
                                hideLoginScreen();
                                showMenuScreen();
                            }
                        }
                    });
            playerChar.isEmpty();

        }
    }

    public void logOut(View view) {
        mAuth.signOut();
        hideMenuScreen();
        showLoginScreen();
    }

    public void fightClick(View view) {
        Intent intent = new Intent(this,GameActivity.class);
        intent.putExtra("char1",playerChar.get(0));
        intent.putExtra("char2",playerChar.get(1));
        intent.putExtra("char3",playerChar.get(2));
        startActivityForResult(intent,1);

    }

    public class ShowData extends AsyncTask<String,Void,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            String[] actualValue = strings[0].split("@");
            databaseReference = database.getReference();
                DatabaseReference dataRef = databaseReference.child(actualValue[0]);
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Menel menel = snapshot.getValue(Menel.class);
                            playerChar.add(menel);
                            Log.i("ShowData", menel.getName());
                        }
                        fillData();
                        Log.i("ShowData","end of data");
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            Log.i("ShowData","SUCCESS");
            return "SUCCESS";
        }
    }
/**
 * Function to hide login screen
 * */
    private void hideLoginScreen()
    {
        logIngEditText.setVisibility(View.INVISIBLE);
        passwordEditText.setVisibility(View.INVISIBLE);
        loginButton.setVisibility(View.INVISIBLE);
        textViewLabel.setVisibility(View.INVISIBLE);
    }
    /**
     * Function to show login screen
     * */
    private void showLoginScreen()
    {
        logIngEditText.setVisibility(View.VISIBLE);
        passwordEditText.setVisibility(View.VISIBLE);
        loginButton.setVisibility(View.VISIBLE);
        textViewLabel.setVisibility(View.VISIBLE);
    }
    /**
     * Function to hide hide menu screen
     * */
    private void hideMenuScreen()
    {
        logoutButton.setVisibility(textViewLabel.INVISIBLE);
        battleButton.setVisibility(View.INVISIBLE);
        char1.setVisibility(View.INVISIBLE);
        char2.setVisibility(View.INVISIBLE);
        char3.setVisibility(View.INVISIBLE);
    }
    /**
     * Function to hide show menu screen
     * */
    private void showMenuScreen()
    {
        logoutButton.setVisibility(textViewLabel.VISIBLE);
        battleButton.setVisibility(View.VISIBLE);
        char1.setVisibility(View.VISIBLE);
        char2.setVisibility(View.VISIBLE);
        char3.setVisibility(View.VISIBLE);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }
    /**
     * Function to fill date for Menel to the view
     * */
    private void fillData()
    {
        //setting data do char1
        TextView name0TextView = findViewById(R.id.name0TextView);
        TextView hp0TextView = findViewById(R.id.hp0TextView);
        TextView agi0TextView = findViewById(R.id.agi0TextView);
        TextView def0TextView = findViewById(R.id.def0TextView);
        TextView str0TextView = findViewById(R.id.str0TextView);
        TextView speed0TextView = findViewById(R.id.speed0TextView);
        TextView lvl0TextView = findViewById(R.id.lvl0TextView);
        ProgressBar exp0ProgressBar = findViewById(R.id.exp0ProgressBar);
        name0TextView.setText(playerChar.get(0).getName());
        hp0TextView.setText("HP:"+Integer.toString(playerChar.get(0).getHp()));
        agi0TextView.setText("AGI"+Integer.toString(playerChar.get(0).getAgi()));
        def0TextView.setText("DEF"+Integer.toString(playerChar.get(0).getDef()));
        str0TextView.setText("STR"+Integer.toString(playerChar.get(0).getStr()));
        speed0TextView.setText("SPEED"+Integer.toString(playerChar.get(0).getSpeed()));
        lvl0TextView.setText("LVL:"+Integer.toString(playerChar.get(0).getLvl()));
        exp0ProgressBar.setMax((int)Math.pow(playerChar.get(0).getLvl(),2)*100);
        exp0ProgressBar.setProgress(playerChar.get(0).getExp());

        //setting data for char2
        TextView name1TextView = findViewById(R.id.name1TextView);
        TextView hp1TextView = findViewById(R.id.hp1TextView);
        TextView agi1TextView = findViewById(R.id.agi1TextView);
        TextView def1TextView = findViewById(R.id.def1TextView);
        TextView str1TextView = findViewById(R.id.str1TextView);
        TextView speed1TextView = findViewById(R.id.speed1TextView);
        TextView lvl1TextView = findViewById(R.id.lvl1TextView);
        ProgressBar exp1ProgressBar = findViewById(R.id.exp1ProgressBar);
        name1TextView.setText(playerChar.get(1).getName());
        hp1TextView.setText("HP:"+Integer.toString(playerChar.get(1).getHp()));
        agi1TextView.setText("AGI"+Integer.toString(playerChar.get(1).getAgi()));
        def1TextView.setText("DEF"+Integer.toString(playerChar.get(1).getDef()));
        str1TextView.setText("STR"+Integer.toString(playerChar.get(1).getStr()));
        speed1TextView.setText("SPEED"+Integer.toString(playerChar.get(1).getSpeed()));
        lvl1TextView.setText("LVL:"+Integer.toString(playerChar.get(1).getLvl()));
        exp1ProgressBar.setMax((int)Math.pow(playerChar.get(1).getLvl(),2)*100);
        exp1ProgressBar.setProgress(playerChar.get(1).getExp());

        //setting data for char3
        TextView nameTextView = findViewById(R.id.nameTextView);
        TextView hpTextView = findViewById(R.id.hpTextView);
        TextView agiTextView = findViewById(R.id.agiTextView);
        TextView defTextView = findViewById(R.id.defTextView);
        TextView strTextView = findViewById(R.id.strTextView);
        TextView speedTextView = findViewById(R.id.speedTextView);
        TextView lvlTextView = findViewById(R.id.lvlTextView);
        ProgressBar expProgressBar = findViewById(R.id.expProgressBar);
        nameTextView.setText(playerChar.get(2).getName());
        hpTextView.setText("HP:"+Integer.toString(playerChar.get(2).getHp()));
        agiTextView.setText("AGI"+Integer.toString(playerChar.get(2).getAgi()));
        defTextView.setText("DEF"+Integer.toString(playerChar.get(2).getDef()));
        strTextView.setText("STR"+Integer.toString(playerChar.get(2).getStr()));
        speedTextView.setText("SPEED"+Integer.toString(playerChar.get(2).getSpeed()));
        lvlTextView.setText("LVL:"+Integer.toString(playerChar.get(2).getLvl()));
        expProgressBar.setMax((int)Math.pow(playerChar.get(2).getLvl(),2)*100);
        expProgressBar.setProgress(playerChar.get(2).getExp());

    }
}
