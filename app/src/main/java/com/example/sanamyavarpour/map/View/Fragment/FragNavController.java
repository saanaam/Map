package com.example.sanamyavarpour.map.View.Fragment;


/**
 * Created by Reza on 12/24/2017.
 */
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.sanamyavarpour.map.R;

import org.json.JSONArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


public class FragNavController {
    public static final int TAB1 = 0;
    public static final int TAB2 = 1;
    public static final int TAB3 = 2;
    public static final int TAB4 = 3;
    public static final int TAB5 = 4;
    private static final String EXTRA_TAG_COUNT = FragNavController.class.getName() + ":EXTRA_TAG_COUNT";
    private static final String EXTRA_SELECTED_TAB_INDEX = FragNavController.class.getName() + ":EXTRA_SELECTED_TAB_INDEX";
    private static final String EXTRA_CURRENT_FRAGMENT = FragNavController.class.getName() + ":EXTRA_CURRENT_FRAGMENT";
    private static final String EXTRA_FRAGMENT_STACK = FragNavController.class.getName() + ":EXTRA_FRAGMENT_STACK";
    @NonNull
    public final List<Stack<Fragment>> mFragmentStacks;
    @IdRes
    private final int mContainerId;
    @NonNull
    private final FragmentManager mFragmentManager;
    public int mSelectedTabIndex;
    private int mTagCount;
    @Nullable
    private Fragment mCurrentFrag;
    @Nullable
    private DialogFragment mCurrentDialogFrag;
    @Nullable
    private RootFragmentListener mRootFragmentListener;
    @Nullable
    private TransactionListener mTransactionListener;
    private int mTransitionMode;
    private boolean mExecutingTransaction;

    public FragNavController(@NonNull FragmentManager fragmentManager, @IdRes int containerId, int numberOfTabs) {
        this.mSelectedTabIndex = -1;
        this.mTransitionMode = -1;
        this.mFragmentManager = fragmentManager;
        this.mContainerId = containerId;
        this.mFragmentStacks = new ArrayList(numberOfTabs);
    }

    public FragNavController(Bundle savedInstanceState, @NonNull FragmentManager fragmentManager, @IdRes int containerId, @NonNull Fragment rootFragment) {
        this(fragmentManager, containerId, 1);
        ArrayList rootFragments = new ArrayList(1);
        rootFragments.add(rootFragment);
        if (!this.restoreFromBundle(savedInstanceState, rootFragments)) {
            Stack stack = new Stack();
            stack.add(rootFragment);
            this.mFragmentStacks.add(stack);
            this.initialize(0);
        }

    }

    public FragNavController(Bundle savedInstanceState, @NonNull FragmentManager fragmentManager, @IdRes int containerId, @NonNull List<Fragment> rootFragments, int startingIndex) {
        this(fragmentManager, containerId, rootFragments.size());
        if (startingIndex > rootFragments.size()) {
            throw new IndexOutOfBoundsException("Starting index cannot be larger than the number of stacks");
        } else {
            if (!this.restoreFromBundle(savedInstanceState, rootFragments)) {
                Iterator var6 = rootFragments.iterator();

                while (var6.hasNext()) {
                    Fragment fragment = (Fragment) var6.next();
                    Stack stack = new Stack();
                    stack.add(fragment);
                    this.mFragmentStacks.add(stack);
                }

                this.initialize(startingIndex);
            }

        }
    }



    public FragNavController(Bundle savedInstanceState, @NonNull FragmentManager fragmentManager, @IdRes int containerId, RootFragmentListener rootFragmentListener, int numberOfTabs, int startingIndex) {
        this(fragmentManager, containerId, numberOfTabs);
        if (startingIndex > numberOfTabs) {
            throw new IndexOutOfBoundsException("Starting index cannot be larger than the number of stacks");
        } else {
            this.setRootFragmentListener(rootFragmentListener);
            if (!this.restoreFromBundle(savedInstanceState, (List) null)) {
                for (int i = 0; i < numberOfTabs; ++i) {
                    this.mFragmentStacks.add(new Stack());
                }

                this.initialize(startingIndex);
            }

        }
    }

    public void setRootFragmentListener(RootFragmentListener rootFragmentListener) {
        this.mRootFragmentListener = rootFragmentListener;
    }

    public void setTransactionListener(TransactionListener transactionListener) {
        this.mTransactionListener = transactionListener;
    }

    public void setTransitionMode(int transitionMode) {
        this.mTransitionMode = transitionMode;
    }

    public void switchTab(int index) throws IndexOutOfBoundsException {
        if (index >= this.mFragmentStacks.size()) {
            throw new IndexOutOfBoundsException("Can\'t switch to a tab that hasn\'t been initialized, Index : " + index + ", current stack size : " + this.mFragmentStacks.size() + ". Make sure to create all of the tabs you need in the Constructor or provide a way for them to be created via RootFragmentListener.");
        } else {
            if (this.mSelectedTabIndex != index) {
                this.mSelectedTabIndex = index;
                FragmentTransaction ft = this.mFragmentManager.beginTransaction();
                ft.setTransition(this.mTransitionMode);
                this.detachCurrentFragment(ft);
                Fragment fragment = this.reattachPreviousFragment(ft);
                if (fragment != null) {
                    ft.commit();
                } else {
                    fragment = this.getRootFragment(this.mSelectedTabIndex);
                    ft.add(this.mContainerId, fragment, this.generateTag(fragment));
                    ft.commit();
                }

                this.executePendingTransactions();
                this.mCurrentFrag = fragment;
                if (this.mTransactionListener != null) {

                    this.mTransactionListener.onTabTransaction(this.mCurrentFrag, this.mSelectedTabIndex);
                }
            } else {
//                if (index == Const.PROFILE_FRAGMENT) {
//
//                    EventBus.getDefault().post((int) Const.PROFILE_FRAGMENT);
//
//                }

            }

        }
    }

    public void pushFragment(@Nullable Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);
            this.detachCurrentFragment(ft);
            ft.add(this.mContainerId, fragment, this.generateTag(fragment));
            ft.commit();
            this.executePendingTransactions();
            ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).push(fragment);
            this.mCurrentFrag = fragment;
            if (this.mTransactionListener != null) {
                this.mTransactionListener.onFragmentTransaction(this.mCurrentFrag);
            }
        }

    }

    public void addFragment (@IdRes int containerId , Fragment fragment){
        if (fragment != null) {
            FragmentTransaction transaction = this.mFragmentManager.beginTransaction();

            transaction.add(containerId, new MapFragment() );

            transaction.commit();
        }
    }


    public void pushFragment(@Nullable Fragment fragment , @IdRes int containerId ) {
        if (fragment != null) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);
            this.detachCurrentFragment(ft);
            ft.add(containerId, fragment, this.generateTag(fragment));
            ft.commit();
            this.executePendingTransactions();
            ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).push(fragment);
            this.mCurrentFrag = fragment;
            if (this.mTransactionListener != null) {
                this.mTransactionListener.onFragmentTransaction(this.mCurrentFrag);
            }
        }

    }

    /**
     * @deprecated
     */
    @Deprecated
    public void push(@Nullable Fragment fragment) {
        this.pushFragment(fragment);
    }

    public void popFragment() throws UnsupportedOperationException {
        this.popFragments(1);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void pop() throws UnsupportedOperationException {
        this.popFragments(1);
    }

    public void popFragments(int popDepth) throws UnsupportedOperationException {
        if (this.isRootFragment()) {
            throw new UnsupportedOperationException("You can not popFragment the rootFragment. If you need to change this fragment, use replaceFragment(fragment)");
        } else if (popDepth < 1) {
            throw new UnsupportedOperationException("popFragments parameter needs to be greater than 0");
        } else if (popDepth >= ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).size() - 1) {
            this.clearStack();
        } else {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);

            Fragment fragment;
            for (int bShouldPush = 0; bShouldPush < popDepth; ++bShouldPush) {
                fragment = this.mFragmentManager.findFragmentByTag(((Fragment) ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).pop()).getTag());
                if (fragment != null) {
                    ft.remove(fragment);
                }
            }

            fragment = this.reattachPreviousFragment(ft);
            boolean var5 = false;
            if (fragment != null) {
                ft.commit();
            } else if (!((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).isEmpty()) {
                fragment = (Fragment) ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).peek();
                ft.add(this.mContainerId, fragment, fragment.getTag());
                ft.commit();
            } else {
                fragment = this.getRootFragment(this.mSelectedTabIndex);
                ft.add(this.mContainerId, fragment, this.generateTag(fragment));
                ft.commit();
                var5 = true;
            }

            this.executePendingTransactions();
            if (var5) {
                ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).push(fragment);
            }

            this.mCurrentFrag = fragment;
            if (this.mTransactionListener != null) {
                this.mTransactionListener.onFragmentTransaction(this.mCurrentFrag);
            }

        }
    }

    public void clearStack() {
        Stack fragmentStack = (Stack) this.mFragmentStacks.get(this.mSelectedTabIndex);
        if (fragmentStack.size() > 1) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);

            Fragment fragment;
            while (fragmentStack.size() > 1) {
                fragment = this.mFragmentManager.findFragmentByTag(((Fragment) fragmentStack.pop()).getTag());
                if (fragment != null) {
                    ft.remove(fragment);
                }
            }

            fragment = this.reattachPreviousFragment(ft);
            boolean bShouldPush = false;
            if (fragment != null) {
                ft.commit();
            } else if (!fragmentStack.isEmpty()) {
                fragment = (Fragment) fragmentStack.peek();
                ft.add(this.mContainerId, fragment, fragment.getTag());
                ft.commit();
            } else {
                fragment = this.getRootFragment(this.mSelectedTabIndex);
                ft.add(this.mContainerId, fragment, this.generateTag(fragment));
                ft.commit();
                bShouldPush = true;
            }

            this.executePendingTransactions();
            if (bShouldPush) {
                ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).push(fragment);
            }

            this.mFragmentStacks.set(this.mSelectedTabIndex, fragmentStack);
            this.mCurrentFrag = fragment;
            if (this.mTransactionListener != null) {
                this.mTransactionListener.onFragmentTransaction(this.mCurrentFrag);
            }
        }

    }

    public void replaceFragment(@NonNull Fragment fragment) {
        Fragment poppingFrag = this.getCurrentFrag();
        if (poppingFrag != null) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);
            Stack fragmentStack = (Stack) this.mFragmentStacks.get(this.mSelectedTabIndex);
            if (!fragmentStack.isEmpty()) {
                fragmentStack.pop();
            }

            String tag = this.generateTag(fragment);
            ft.replace(this.mContainerId, fragment, tag);
            ft.commit();
            this.executePendingTransactions();
            fragmentStack.push(fragment);
            this.mCurrentFrag = fragment;
            if (this.mTransactionListener != null) {
                this.mTransactionListener.onFragmentTransaction(this.mCurrentFrag);
            }
        }

    }

    /**
     * @deprecated
     */
    public void replace(@NonNull Fragment fragment) {
        this.replaceFragment(fragment);
    }

    private void initialize(int index) {
        this.mSelectedTabIndex = index;
        this.clearFragmentManager();
        this.clearDialogFragment();
        FragmentTransaction ft = this.mFragmentManager.beginTransaction();
        ft.setTransition(this.mTransitionMode);
        Fragment fragment = this.getRootFragment(index);
        ft.add(this.mContainerId, fragment, this.generateTag(fragment));
        ft.commit();
        this.executePendingTransactions();
        this.mCurrentFrag = fragment;
        if (this.mTransactionListener != null) {
            this.mTransactionListener.onTabTransaction(this.mCurrentFrag, this.mSelectedTabIndex);
        }

    }

    @NonNull
    @CheckResult
    private Fragment getRootFragment(int index) throws IllegalStateException {
        Fragment fragment = null;
        if (!((Stack) this.mFragmentStacks.get(index)).isEmpty()) {
            fragment = (Fragment) ((Stack) this.mFragmentStacks.get(index)).peek();
        } else if (this.mRootFragmentListener != null) {
            fragment = this.mRootFragmentListener.getRootFragment(index);
            ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).push(fragment);
        }

        if (fragment == null) {
            throw new IllegalStateException("Either you haven\'t past in a fragment at this index in your constructor, or you haven\'tprovided a way to create it while via your RootFragmentListener.getRootFragment(index)");
        } else {
            return fragment;
        }
    }

    @Nullable
    private Fragment reattachPreviousFragment(@NonNull FragmentTransaction ft) {
        Stack fragmentStack = (Stack) this.mFragmentStacks.get(this.mSelectedTabIndex);
        Fragment fragment = null;
        if (!fragmentStack.isEmpty()) {
            fragment = this.mFragmentManager.findFragmentByTag(((Fragment) fragmentStack.peek()).getTag());
            if (fragment != null) {
                ft.attach(fragment);
            }
        }

        return fragment;
    }

    private void detachCurrentFragment(@NonNull FragmentTransaction ft) {
        Fragment oldFrag = this.getCurrentFrag();
        if (oldFrag != null) {
            ft.detach(oldFrag);
        }

    }

    @Nullable
    @CheckResult
    public Fragment getCurrentFrag() {
        if (this.mCurrentFrag != null) {
            return this.mCurrentFrag;
        } else {
            Stack fragmentStack = (Stack) this.mFragmentStacks.get(this.mSelectedTabIndex);
            if (!fragmentStack.isEmpty()) {
                this.mCurrentFrag = this.mFragmentManager.findFragmentByTag(((Fragment) ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).peek()).getTag());
            }

            return this.mCurrentFrag;
        }
    }

    @NonNull
    @CheckResult
    private String generateTag(@NonNull Fragment fragment) {
        return fragment.getClass().getName() + ++this.mTagCount;
    }

    private void executePendingTransactions() {
        if (!this.mExecutingTransaction) {
            this.mExecutingTransaction = true;
            try {
                this.mFragmentManager.executePendingTransactions();
                this.mExecutingTransaction = false;
            } catch (Exception e) {

            }

        }

    }

    private void clearFragmentManager() {
        if (this.mFragmentManager.getFragments() != null) {
            FragmentTransaction ft = this.mFragmentManager.beginTransaction();
            ft.setTransition(this.mTransitionMode);
            Iterator var2 = this.mFragmentManager.getFragments().iterator();

            while (var2.hasNext()) {
                Fragment fragment = (Fragment) var2.next();
                if (fragment != null) {
                    ft.remove(fragment);
                }
            }

            ft.commit();
            this.executePendingTransactions();
        }

    }

    @CheckResult
    public int getSize() {
        return this.mFragmentStacks.size();
    }

    @CheckResult
    @NonNull
    public Stack<Fragment> getCurrentStack() {
        return (Stack) ((Stack) this.mFragmentStacks.get(this.mSelectedTabIndex)).clone();
    }

    /**
     * @deprecated
     */
    @Deprecated
    @CheckResult
    public boolean canPop() {
        return this.getCurrentStack().size() > 1;
    }

    @CheckResult
    public boolean isRootFragment() {
        return this.getCurrentStack().size() == 1;
    }

    @Nullable
    @CheckResult
    public DialogFragment getCurrentDialogFrag() {
        if (this.mCurrentDialogFrag != null) {
            return this.mCurrentDialogFrag;
        } else {
            FragmentManager fragmentManager;
            if (this.mCurrentFrag != null) {
                fragmentManager = this.mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = this.mFragmentManager;
            }

            if (fragmentManager.getFragments() != null) {
                Iterator var2 = fragmentManager.getFragments().iterator();

                while (var2.hasNext()) {
                    Fragment fragment = (Fragment) var2.next();
                    if (fragment instanceof DialogFragment) {
                        this.mCurrentDialogFrag = (DialogFragment) fragment;
                        break;
                    }
                }
            }

            return this.mCurrentDialogFrag;
        }
    }

    public void clearDialogFragment() {
        if (this.mCurrentDialogFrag != null) {
            this.mCurrentDialogFrag.dismiss();
            this.mCurrentDialogFrag = null;
        } else {
            FragmentManager fragmentManager;
            if (this.mCurrentFrag != null) {
                fragmentManager = this.mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = this.mFragmentManager;
            }

            if (fragmentManager.getFragments() != null) {
                Iterator var2 = fragmentManager.getFragments().iterator();

                while (var2.hasNext()) {
                    Fragment fragment = (Fragment) var2.next();
                    if (fragment instanceof DialogFragment) {
                        ((DialogFragment) fragment).dismiss();
                    }
                }
            }
        }

    }

    public void showDialogFragment(@Nullable DialogFragment dialogFragment) {
        if (dialogFragment != null) {
            FragmentManager fragmentManager;
            if (this.mCurrentFrag != null) {
                fragmentManager = this.mCurrentFrag.getChildFragmentManager();
            } else {
                fragmentManager = this.mFragmentManager;
            }

            if (fragmentManager.getFragments() != null) {
                Iterator var3 = fragmentManager.getFragments().iterator();

                while (var3.hasNext()) {
                    Fragment fragment = (Fragment) var3.next();
                    if (fragment instanceof DialogFragment) {
                        ((DialogFragment) fragment).dismiss();
                        this.mCurrentDialogFrag = null;
                    }
                }
            }

            this.mCurrentDialogFrag = dialogFragment;

            try {
                dialogFragment.show(fragmentManager, dialogFragment.getClass().getName());
            } catch (IllegalStateException var5) {
                ;
            }
        }

    }

    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(EXTRA_TAG_COUNT, this.mTagCount);
        outState.putInt(EXTRA_SELECTED_TAB_INDEX, this.mSelectedTabIndex);
        if (this.mCurrentFrag != null) {
            outState.putString(EXTRA_CURRENT_FRAGMENT, this.mCurrentFrag.getTag());
        }

        try {
            JSONArray stackArrays = new JSONArray();
            Iterator var3 = this.mFragmentStacks.iterator();

            while (var3.hasNext()) {
                Stack stack = (Stack) var3.next();
                JSONArray stackArray = new JSONArray();
                Iterator var6 = stack.iterator();

                while (var6.hasNext()) {
                    Fragment fragment = (Fragment) var6.next();
                    stackArray.put(fragment.getTag());
                }

                stackArrays.put(stackArray);
            }

            outState.putString(EXTRA_FRAGMENT_STACK, stackArrays.toString());
        } catch (Throwable var8) {
            ;
        }

    }

    private boolean restoreFromBundle(@Nullable Bundle savedInstanceState, @Nullable List<Fragment> rootFragments) {
        if (savedInstanceState == null) {
            return false;
        } else {
            this.mTagCount = savedInstanceState.getInt(EXTRA_TAG_COUNT, 0);
            this.mCurrentFrag = this.mFragmentManager.findFragmentByTag(savedInstanceState.getString(EXTRA_CURRENT_FRAGMENT));

            try {
                JSONArray t = new JSONArray(savedInstanceState.getString(EXTRA_FRAGMENT_STACK));

                for (int x = 0; x < t.length(); ++x) {
                    JSONArray stackArray = t.getJSONArray(x);
                    Stack stack = new Stack();
                    if (stackArray.length() != 1) {
                        for (int var11 = 0; var11 < stackArray.length(); ++var11) {
                            String var12 = stackArray.getString(var11);
                            if (var12 != null && !"null".equalsIgnoreCase(var12)) {
                                Fragment fragment = this.mFragmentManager.findFragmentByTag(var12);
                                if (fragment != null) {
                                    stack.add(fragment);
                                }
                            }
                        }
                    } else {
                        String y = stackArray.getString(0);
                        Fragment tag;
                        if (y != null && !"null".equalsIgnoreCase(y)) {
                            tag = this.mFragmentManager.findFragmentByTag(y);
                        } else if (rootFragments != null) {
                            tag = (Fragment) rootFragments.get(x);
                        } else {
                            tag = this.getRootFragment(x);
                        }

                        if (tag != null) {
                            stack.add(tag);
                        }
                    }

                    this.mFragmentStacks.add(stack);
                }

                switch (savedInstanceState.getInt(EXTRA_SELECTED_TAB_INDEX)) {
                    case 0:
                        this.switchTab(0);
                        break;
                    case 1:
                        this.switchTab(1);
                        break;
                    case 2:
                        this.switchTab(2);
                        break;
                    case 3:
                        this.switchTab(3);
                        break;
                    case 4:
                        this.switchTab(4);
                }

                return true;
            } catch (Throwable var10) {
                return false;
            }
        }
    }

    public interface TransactionListener {
        void onTabTransaction(Fragment var1, int var2);

        void onFragmentTransaction(Fragment var1);
    }

    public interface RootFragmentListener {
        Fragment getRootFragment(int var1);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface TabIndex {
    }
}