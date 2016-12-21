package RL.XOWorld;

import RL.State;

/**
 * Created by max on 11/12/2016.
 */
public class XOState extends State {

    public XOBoard identity;

    public XOState(XOBoard identity) {
        super();
        this.identity = identity;
    }

    public String toString() {
        return identity.toString();
    }

    @Override
    public XOBoard getStateIdentity() {
        return identity;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof XOState) {
            return ((XOState) o).getStateIdentity().equals(identity);
        }
        return false;
    }
}
