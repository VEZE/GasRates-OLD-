package wiksinc.currencyrates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class CustomActivity extends AppCompatActivity implements View.OnClickListener {
    private RadioGroup radioGroup;
    private Button btn;
    public String azsID;
    public String Summ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_layout);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btn=(Button)findViewById(R.id.button);
        btn.setOnClickListener(this);
        }



    @Override
    public void onClick(View view) {

       //radioGroup.clearCheck();
        int checker = radioGroup.getCheckedRadioButtonId();

        if (checker !=-1) {
            RadioButton btn1 = (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
             azsID = ((TextView) findViewById(R.id.azsID)).getText().toString();
            if (azsID.equals(Integer.toString(0)) ) {

                Toast.makeText(CustomActivity.this,"Номер Колонки не может быть равен 0",Toast.LENGTH_SHORT).show();

            }
            else {
                Summ = ((TextView) findViewById(R.id.Summ)).getText().toString();

                // Toast.makeText(CustomActivity.this, btn1.getText()+"  "+azsID+" "+Summ, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("name", btn1.getText() + "  номер колонки=" + azsID + "  сумма=" + Summ);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        else
            Toast.makeText(CustomActivity.this,"вы ничего не выбрали",Toast.LENGTH_SHORT).show();
    }
}





