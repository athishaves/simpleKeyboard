package com.athishworks.keyboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.core.content.ContextCompat;

public class SimpleKeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard mainKeyboard, firstKeyboard, numberKeyboard;

    private int capsState = 1;

    private static final int KEYBOARD_MAIN_STICKY = -15;
    private static final int KEYBOARD_CHARACTER_STICKY = -16;


    private int KEYBOARD_ACTION;
    private boolean multiLine;


    private static final int KEYBOARD_SHIFT_KEY = -10;
    private static final int KEYBOARD_DONE_KEY = -4;
    private Keyboard.Key shiftKey, doneKey;

    private int deleteTimes;
    private boolean deleteCalledNow = false;

    private int imeOptions;


    @Override
    public void onStartInputView(EditorInfo info, boolean restarting) {
        super.onStartInputView(info, restarting);

        deleteTimes = 1;
        imeOptions = info.imeOptions;

        switch (info.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER:
            case InputType.TYPE_CLASS_DATETIME:
            case InputType.TYPE_CLASS_PHONE:
                capsState = 0;
                changeKeyboard(numberKeyboard);
                break;

            default:
                changeKeyboard(mainKeyboard);
                if (info.initialCapsMode != 0) capsState = 1;
                changeCapsState(capsState);
        }


        multiLine = (info.inputType & 0xf0000) == InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        if (multiLine) doneKey.label = "Enter";
    }



    private void changeKeyboard(Keyboard keyboard) {
        kv.setKeyboard(keyboard);

        for (Keyboard.Key key : keyboard.getKeys()) {
            if (key.codes[0] == KEYBOARD_SHIFT_KEY) shiftKey = key;
            if (key.codes[0] == KEYBOARD_DONE_KEY) doneKey = key;
        }

        switch (imeOptions & EditorInfo.TYPE_MASK_CLASS) {
            case (EditorInfo.IME_ACTION_NEXT):
                KEYBOARD_ACTION = EditorInfo.IME_ACTION_NEXT;    doneKey.label = "NEXT";
                break;
            case (EditorInfo.IME_ACTION_DONE):
                KEYBOARD_ACTION = EditorInfo.IME_ACTION_DONE;    doneKey.label = "DONE";
                break;
            case (EditorInfo.IME_ACTION_SEND):
                KEYBOARD_ACTION = EditorInfo.IME_ACTION_SEND;    doneKey.label = "SEND";
                break;
            case (EditorInfo.IME_ACTION_SEARCH):
                KEYBOARD_ACTION = EditorInfo.IME_ACTION_SEARCH;    doneKey.label = "SEARCH";
                break;
            case (EditorInfo.IME_ACTION_GO):
                KEYBOARD_ACTION = EditorInfo.IME_ACTION_GO;    doneKey.label = "GO";
                break;
        }
    }



    @Override
    public View onCreateInputView() {
        mainKeyboard = new Keyboard(this,R.xml.qwerty);
        firstKeyboard = new Keyboard(this,R.xml.characters);
        numberKeyboard = new Keyboard(this,R.xml.number);

        kv = (KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);
        kv.setPreviewEnabled(false);
        kv.setOnKeyboardActionListener(this);
        return kv;
    }



    @Override
    public void onPress(int i) {
        if (i != Keyboard.KEYCODE_DELETE) return;
        if (deleteCalledNow) deleteTimes = 1;
        deleteCalledNow = true;
    }



    @Override
    public void onRelease(int i) { }



    @Override
    public void onKey(int i, int[] ints) {
        InputConnection ic = getCurrentInputConnection();
        switch (i)
        {
            case KEYBOARD_MAIN_STICKY:
                changeKeyboard(firstKeyboard);
                break;

            case KEYBOARD_CHARACTER_STICKY:
                changeKeyboard(mainKeyboard);
                break;

            case Keyboard.KEYCODE_DELETE:
                int a = (int) Math.ceil((float)deleteTimes/3);
                ic.deleteSurroundingText(a,0);
                deleteTimes++;
                break;

            case KEYBOARD_SHIFT_KEY:
                changeCapsState((capsState+1)%3);
                break;

            case Keyboard.KEYCODE_DONE:
                if (multiLine) {
                    ic.commitText("\n", 0);
                    if (capsState == 0) changeCapsState(1);
                }
                else ic.performEditorAction(KEYBOARD_ACTION);
                break;

            default:
                char code = (char)i;
                if(Character.isLetter(code) && capsState!=0) code = Character.toUpperCase(code);
                ic.commitText(String.valueOf(code),1);

                if(code == ' ' && kv.getKeyboard()==firstKeyboard) changeKeyboard(mainKeyboard);
                if(capsState==1) changeCapsState(0);
        }

        if (i!=Keyboard.KEYCODE_DELETE) deleteTimes = 1;
    }



    private void changeCapsState(int state) {
        capsState = state;
        mainKeyboard.setShifted(state!=0);

        int drawable = (state==0) ? R.drawable.small_char : ((state==1) ? R.drawable.medium_char : R.drawable.big_char);
        if(shiftKey!=null) shiftKey.icon = ContextCompat.getDrawable(this,drawable);

        kv.invalidateAllKeys();
    }



    @Override
    public void onText(CharSequence charSequence) {}



    @Override
    public void swipeLeft() { }

    @Override
    public void swipeRight() { }

    @Override
    public void swipeDown() { }

    @Override
    public void swipeUp() { }

}