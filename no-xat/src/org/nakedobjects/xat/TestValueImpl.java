package org.nakedobjects.xat;


import org.nakedobjects.object.InvalidEntryException;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.ValueParseException;
import org.nakedobjects.object.reflect.ValueFieldSpecification;


public class TestValueImpl implements TestValue {
    private final NakedObject parent;
    private final ValueFieldSpecification value;
    
    public TestValueImpl(NakedObject parent, ValueFieldSpecification value) {
        this.parent = parent;
        this.value = value;
        setForObject(value.get(parent));
    }

    public void fieldEntry(String value) {
         try {
             this.value.parseAndSave(parent, value);
        } catch (ValueParseException e) {
            throw new IllegalActionError("Field value '" + value + "' could not be parsed in field " + this.value.getName());
        } catch (InvalidEntryException e) {
            throw new IllegalActionError("Field value '" + value + "' is not valid: " + e.getMessage());
        }
    }

    /**
     returns the title of the object as a String
     */
    public String getTitle() {
        return getForObject().titleString().toString();
    }

    public Naked getForObject() {
        return value.get(parent);
    }

    public void setForObject(Naked object) {
    }
}

/*
Naked Objects - a framework that exposes behaviourally complete
business objects directly to the user.
Copyright (C) 2000 - 2003  Naked Objects Group Ltd

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

The authors can be contacted via www.nakedobjects.org (the
registered address of Naked Objects Group is Kingsway House, 123 Goldworth
Road, Woking GU21 1NR, UK).
*/