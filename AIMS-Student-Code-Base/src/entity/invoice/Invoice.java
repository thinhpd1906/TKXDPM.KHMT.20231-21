package entity.invoice;

import entity.order.Order;
import org.mockito.internal.matchers.Or;
//low coupling - data coupling because you should use getter, setter for urlPayOrder
public class Invoice {

    private Order order;
    private int amount;

    private String urlPayOrder;
    
    public Invoice(){

    }

    public Invoice(Order order){
        this.order = order;
    }

    public Invoice(Order order, String urlPayOrder){this.order = order; this.urlPayOrder = urlPayOrder;}

    public Order getOrder() {
        return order;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public String getUrlPayOrder(){ return this.urlPayOrder;}

    public void saveInvoice(){
        
    }
}
