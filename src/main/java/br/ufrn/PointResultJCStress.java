package br.ufrn;

import br.ufrn.point.SequentialPoint;
import org.openjdk.jcstress.annotations.Result;
import sun.misc.Contended;

import java.io.Serializable;

@Result
public class PointResultJCStress implements Serializable {
    @Contended
    @jdk.internal.vm.annotation.Contended

    public SequentialPoint point;

    public PointResultJCStress() {
    }

    public int hashCode() {
        return this.toString().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            PointResultJCStress that = (PointResultJCStress) o;
            if(this.point.equalsTo(that.point)) return false;
            return true;
        } else {
            return false;
        }
    }

    public String toString() {
        return point.toString();
    }
}
