//package entity;
//
//import java.util.List;
//
//public class BossPattern {
//
//    private final double pullConstant = 0.005;
//
//    /** Pull attack pattern */
//    public void blackHolePattern(List<Ship> ships, final int cx, final int cy, final int radius) {
//        for(Ship ship : ships){
//            double sx = ship.positionX;
//            double sy = ship.positionY;
//
//            double dx = cx - sx;
//            double dy = cy - sy;
//
//            double dist =  Math.sqrt(dx * dx + dy * dy);
//            if(dist <= radius && dist > 1){
//                double force = (radius - dist) * pullConstant;
//
//                double ux = dx/dist;
//                double uy = dy/dist;
//
//                ship.positionX += (int)(ux * force);
//                ship.positionY += (int)(uy * force);
//
//            }
//        }
//    }
//}
