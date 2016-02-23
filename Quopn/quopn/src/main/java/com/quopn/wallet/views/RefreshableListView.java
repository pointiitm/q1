package com.quopn.wallet.views;

/**
 * @author Sumeet
 *
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quopn.wallet.R;

public class RefreshableListView extends GridView implements OnScrollListener {

	private final static int RELEASE_To_REFRESH = 0;// The drop-down process
													// state value
	private final static int PULL_To_REFRESH = 1; // From the drop down to
													// return to the no refresh
													// status value
	private final static int REFRESHING = 2;// Refreshing the state value
	private final static int DONE = 3;
	private final static int LOADING = 4;

	// The offset distance of the ratio of distance and the interface of the
	// actual padding
	private final static int RATIO = 2;
	private LayoutInflater inflater;

	// ListView head pull down to refresh the layout
	private LinearLayout headerView;
	private TextView headerText;

	// Define the head pull down to refresh the layout of the height
	private int headerContentHeight;

	private int startY;
	private int state;
	private boolean isBack;

	// For guarantee in a complete touch event is recorded only once the value
	// of startY
	private boolean isRecored;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public RefreshableListView(Context context) {
		super(context);
		init(context);
	}

	public RefreshableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setCacheColorHint(context.getResources().getColor(R.color.transparent));
		inflater = LayoutInflater.from(context);
		headerView = (LinearLayout) inflater.inflate(R.layout.lv_header, null);

		headerText = (TextView) headerView.findViewById(R.id.headerText);

		measureView(headerView);
		headerContentHeight = headerView.getMeasuredHeight();
		// Sets the padding, just from the top as a negative throughout the
		// height of the layout, just to the head.
		headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
		// Redraw.
		headerView.invalidate();
		// The top will pull down to refresh the layout to join ListView
		//addHeaderView(headerView, null, false); // old code commented out as for new design no header is needed.
		setOnScrollListener(this);
		
		// The start state is pull down to refresh after state, so is DONE
		state = DONE;
		// Whether it is refresh
		isRefreshable = false;
	}

	public void setHeaderText(String headertext) {
		headerText.setText(headertext);
	}
	
	public TextView getHeaderTextView(){
		return headerText;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefreshable) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!isRecored) {
					isRecored = true;
					startY = (int) ev.getY();// The fingers pressed records the
												// current position
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state != REFRESHING && state != LOADING) {
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onLvRefresh();
					}
				}
				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();
				if (!isRecored) {
					isRecored = true;
					startY = tempY;
				}
				if (state != REFRESHING && isRecored && state != LOADING) {
					// The process is set in padding, the current location has
					// been in head, or if the list beyond the screen, when
					// pushed, the list will be carried out at the same time
					// rolling
					// You can go to refresh.
					if (state == RELEASE_To_REFRESH) {
						setSelection(0);
						// Push upwards, to screen enough to mask the degree of
						// head, but not to all cover up.
						if (((tempY - startY) / RATIO < headerContentHeight)// By
																			// loosening
																			// the
																			// refresh
																			// state
																			// to
																			// pull
																			// down
																			// to
																			// refresh
																			// the
																			// state
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
						// All of a sudden push to the top
						else if (tempY - startY <= 0) {// By loosening refresh
														// state transition to
														// the done state
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// Still do not reach the display refresh time release, DONE
					// or PULL_To_REFRESH state
					if (state == PULL_To_REFRESH) {
						setSelection(0);
						// Drop down to the can enter the RELEASE_TO_REFRESH
						// state
						if ((tempY - startY) / RATIO >= headerContentHeight) {// By
																				// done
																				// or
																				// pull
																				// down
																				// to
																				// refresh
																				// state
																				// to
																				// loosen
																				// the
																				// refresh
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();
						}
						// Push the top
						else if (tempY - startY <= 0) {// By DOne or pull down
														// to refresh state
														// transition to the
														// done state
							state = DONE;
							changeHeaderViewByState();
						}
					}
					// The done state
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}
					// Update headView size
					if (state == PULL_To_REFRESH) {
						headerView.setPadding(0, -1 * headerContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}
					// Update headView paddingTop
					if (state == RELEASE_To_REFRESH) {
						headerView.setPadding(0, (tempY - startY) / RATIO
								- headerContentHeight, 0, 0);
					}

				}
				break;

			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	// When the state changes times, calling the method, to update the interface
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			break;
		case PULL_To_REFRESH:
			
			break;

		case REFRESHING:
			headerView.setPadding(0, 0, 0, 0);
			break;
		case DONE:
			headerView.setPadding(0, -1 * headerContentHeight, 0, 0);
			break;
		}
	}

	// This method directly copy to refresh a drop-down on the network demo,
	// this is � &rdquo estimation; headView width and height
	private void measureView(View child) {
		ViewGroup.LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0,
				params.width);
		int lpHeight = params.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		changeHeaderViewByState();
	}

	private void onLvRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			isRefreshable = true;
		} else {
			isRefreshable = false;
		}

	}
}
