package xj.mobile.model.properties

/*
 *  Generated by xj.mobile.tool.ProcessType
 *  Created on: Wed Jul 17 14:24:21 CDT 2013
 */
class SwipeGestureRecognizerDirection extends Property { 

  static values = [:]
  static names = [ 'Right', 'Left', 'Up', 'Down' ]

  static final SwipeGestureRecognizerDirection Right = new SwipeGestureRecognizerDirection('Right')
  static final SwipeGestureRecognizerDirection Left = new SwipeGestureRecognizerDirection('Left')
  static final SwipeGestureRecognizerDirection Up = new SwipeGestureRecognizerDirection('Up')
  static final SwipeGestureRecognizerDirection Down = new SwipeGestureRecognizerDirection('Down')

  String name
  
  private SwipeGestureRecognizerDirection(name) { 
    this.name = name
    values[name] = this
  }

  String toIOSString() { 
    "UISwipeGestureRecognizerDirection${name}"
  }

  String toAndroidJavaString() { 
    "SwipeGestureDirection${name}"
  }

  String toShortString() { 
    name
  }

  String toString() { 
    "SwipeGestureRecognizerDirection.${name}"
  }

  static boolean isCompatible(value) { 
	(value instanceof String) || 
	(value instanceof List) 
  }

  static boolean hasValue(name) { 
    values.hasKey(name)
  }

  static SwipeGestureRecognizerDirection getValue(name) { 
    values[name]
  }

}