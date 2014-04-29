package annoyaml.util;

import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedList;

public class CycleCheck {
	private Deque<IdentityHashMap<Object, Boolean>> stack = new LinkedList<IdentityHashMap<Object, Boolean>>();
	
	public void push() {
		IdentityHashMap<Object, Boolean> stackFrame = new IdentityHashMap<Object, Boolean>();
		stack.push(stackFrame);
	}
	
	public void addObject(Object obj) {
		IdentityHashMap<Object, Boolean> stackFrame = stack.peek();
		stackFrame.put(obj, true);
	}
	
	public boolean cycleExists(Object obj) {
		for (IdentityHashMap<Object, Boolean> stackFrame : stack) {
			Boolean containsObj = stackFrame.get(obj);
			if (containsObj != null && containsObj) {
				return true;
			}
		}
		return false;
	}
	
	public void pop() {
		stack.pop();
	}
}
