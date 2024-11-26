package modules;

import exceptions.IncorrectIdRuntimeException;
import lombok.Getter;
import lombok.Setter;
import modules.robot.*;
import service.Sorting;
import service.operate.InvestorMenager;
import service.operate.SellerMenager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private double gold;
    private String nick;
    private Machine machine;
    private List<Investor> ownedInvestors = new ArrayList<>();
    private List<AbstractSeller> ownedSellers = new ArrayList<>();

    public User(String nick){
        this.nick=nick;
        gold=100000;
    }

    @Override
    public String toString() {
        return this.nick;
    }

    public Investor giveIdInvestor(Investor investor){
        for (int i = 0; i < ownedInvestors.size(); i++) {
            if(ownedInvestors.get(i).getInvId()!=i){
                investor.setInvId(i);
                return investor;
            }
        }
        investor.setInvId(ownedInvestors.size());
        return investor;
    }

    public AbstractSeller giveIdSeller(AbstractSeller seller){
        int length = ownedSellers.size();
        for (int i = 0; i < length; i++) {
            if(ownedSellers.get(i).getSellerId()!=i){
                seller.setSellerId(i);
                return seller;
            }
        }
        seller.setSellerId(length);
        return seller;
    }

    public <T extends AbstractRobot> void addToList (T type){
        if (type instanceof Investor) {
            ownedInvestors.add(giveIdInvestor((Investor) type));
            Investor.setQuantityInv(ownedInvestors.size());
            Sorting.sortListInvestor();
        }else if (type instanceof AbstractSeller){
            ownedSellers.add(giveIdSeller((AbstractSeller) type));
            SellerBooks.setQuantitySel(ownedSellers.size());
            Sorting.sortListSeller();
            if(type instanceof SellerBooks)
                SellerBooks.setSellerBookQuantity(SellerBooks.getSellerBookQuantity()+1);
            if(type instanceof SellerBoardGames)
                SellerBoardGames.setSellerBoardGamesQuantity(SellerBoardGames.getSellerBoardGamesQuantity()+1);
            if(type instanceof SellerComputerGames)
                SellerComputerGames.setSellerComputerGamesQuantity(SellerComputerGames.getSellerComputerGamesQuantity()+1);
            if(type instanceof SellerHouses)
                SellerHouses.setSellerHousesQuantity(SellerHouses.getSellerHousesQuantity() + 1);
        }
    }

    public <T extends AbstractRobot> void removeFromList (Class<T> type, int id) {
        if (type.equals(Investor.class)) {
            List ownedInvestorsCopy = new ArrayList(ownedInvestors);
            if (ownedInvestors.contains(InvestorMenager.findInvestorById(id))) {
                ownedInvestors.forEach((k) ->{
                    if(k.getInvId()==id){
                        ownedInvestorsCopy.remove(k);
                    }
                });
                ownedInvestors=ownedInvestorsCopy;
                Investor.setQuantityInv(ownedInvestors.size());
            } else
                throw new IncorrectIdRuntimeException();
        }
        if (AbstractSeller.class.isAssignableFrom(type)) {
            if (ownedSellers.contains(SellerMenager.findSellerById(id).get())) {
                List<AbstractSeller> ownedSellersCopy = new ArrayList<>();
                ownedSellers.forEach((v)->{
                    if(v.getSellerId()!=id){
                        ownedSellersCopy.add(v);
                    }
                });
                ownedSellers=ownedSellersCopy;
                AbstractSeller.setQuantitySel(ownedSellers.size());
                if(type.equals(SellerBooks.class))
                    SellerBooks.setSellerBookQuantity(ownedSellers.size());
                if(type==SellerBoardGames.class)
                    SellerBoardGames.setSellerBoardGamesQuantity(ownedSellers.size());
                if(type==SellerComputerGames.class)
                    SellerComputerGames.setSellerComputerGamesQuantity(ownedSellers.size());
                if(type==SellerHouses.class)
                    SellerHouses.setSellerHousesQuantity(ownedSellers.size());
            } else
                throw new IncorrectIdRuntimeException();
        }
    }

    public void unlockMachine(){
        machine = new Machine();
    }

    public static void saveUser(List<User> userList, String fileName) throws IOException {
        try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))){
            output.writeObject(userList);
        }
    }

    public static List<User> loadUser(String fileName) throws IOException, ClassNotFoundException{
        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))){
            return (List<User>) input.readObject();
        }
    }
}

