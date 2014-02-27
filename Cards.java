package Poker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Cards
{
	static class Player
	{
		int chips = 500;
		String[] hand = new String[2];
		Player(){}
	}
	
	static int nextCard = -1;
	static String draw()
	{
		nextCard++;
		return deck[nextCard];
	}
	
	static String deck[] = new String[52];
	static void shuffle()
	{
		deck = new String[52]; nextCard = -1; //resets deck
		Random rand = new Random();
		String suits[] = {"Spades", "Hearts", "Clubs", "Diamonds"};
		String numbs[] = {"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
		for (int s = 0; s < suits.length; s++)
		{
			for (int n = 0; n < numbs.length; n++)
			{
				int randNum = rand.nextInt(52);
				while (deck[randNum] != null) randNum = rand.nextInt(52);
				deck[randNum] = numbs[n] + " of " +suits[s];
			}
		}
	}
	
	public static void main(String[] args)
	{
		//texasHoldEm();
		videoPoker();
	}
	
	static int payout(String combo)
	{
		switch (combo.split(":")[0])
		{
			case "One Pair": 	   return -1;
			//check for jacks pair return 0;
			case "Two Pair": 	   return 2;
			case "Three of a Kind":return 3;
			case "Straight": 	   return 4;
			case "Flush": 		   return 6;
			case "Full House": 	   return 9;
			case "Four of a Kind": return 25;
			case "Straight Flush": return 50;
			default : return 0;
		}
	}
	
	static void vpThreeOfKind(String[] hand, String card)
	{
		String threeOfKindRemove = "";
		for (int i = 0; i < hand.length; i++) 
			if (hand[i].split(" of")[0].equals(card))
				threeOfKindRemove = threeOfKindRemove.concat(Integer.toString(i));
	}
	
	static int countTwoPair = 0;
	static void vpTwoPair(String[] hand, String card)
	{
		
		for (int i = 0; i < hand.length; i++) 
			if (!hand[i].split(" of")[0].equals(card.split(" ")[0]) &&
				!hand[i].split(" of")[0].equals(card.split(" ")[1]) ) hand[i] = draw();
		
		String twoPairCombo = "";
		ArrayList<String> handAList = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) handAList.add(hand[i]);
		twoPairCombo = identify(handAList);
		countTwoPair += payout(twoPairCombo.split(":")[0]);
	}
	
	static int countLowPairOneHigh = 0;
	static int countLowPair = 0;
	static int countOneHigh = 0;
	static void vpLowPairOneHigh(String[] hand, String card)
	{
		String keepLowPairOneHigh = "";
		String keepLowPair = "";
		String keepOneHigh = "";
		
		for (int i = 0; i < 5; i++)//only two pair
		{
			if (Integer.parseInt(hand[i].split(" of")[0]) > 10) keepLowPair = keepLowPair.concat(Integer.toString(i));
			else if (!hand[i].split(" of")[0].equals(card)) keepLowPairOneHigh = keepLowPairOneHigh.concat(Integer.toString(i));
			else keepOneHigh = keepOneHigh.concat(Integer.toString(i));
		}
		
		String lowPairOneHighCombo = "";
		for (int i = 0; i < 2; i++) hand[Character.getNumericValue(keepLowPairOneHigh.charAt(i))] = draw();//replace
		ArrayList<String> handAList = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) handAList.add(hand[i]);
		lowPairOneHighCombo = identify(handAList);
		countLowPairOneHigh += payout(lowPairOneHighCombo.split(":")[0]);
		
		String lowPairCombo = "";
		String backupHighCard = hand[Integer.parseInt(keepLowPair)];
		hand[Integer.parseInt(keepLowPair)] = draw();
		handAList = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) handAList.add(hand[i]);
		lowPairCombo = identify(handAList);
		countLowPair += payout(lowPairCombo.split(":")[0]);
		
		String oneHighCombo = "";
		hand[Integer.parseInt(keepLowPair)] = backupHighCard;
		for (int i = 0; i < 2; i++) hand[Character.getNumericValue(keepOneHigh.charAt(i))] = draw();
		handAList = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) handAList.add(hand[i]);
		oneHighCombo = identify(handAList);
		countOneHigh += payout(oneHighCombo.split(":")[0]); 
	}
	
	static void videoPoker()
	{
		String[] hand = new String[5];
		String combo = "";

		for (int run = 0; run < 1000; run++)
		{
		shuffle();
		for (int i = 0; i < 5; i++) hand[i] = draw();
		//System.out.println(Arrays.toString(hand));
		
		ArrayList<String> handAList = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) handAList.add(hand[i]);
		
		boolean threeHighCards = false;
		boolean twoHighCards = false;
		boolean oneHighCard = false;
		
		int highCardCount = 0;
		for (int i = 0; i < hand.length; i++) if (Integer.parseInt(hand[i].split(" of")[0]) > 10) highCardCount++;
		switch (highCardCount)
		{
			case 1:{oneHighCard = true; break;}   //A....
			case 2:{twoHighCards = true; break;}  //AK...
			case 3:{threeHighCards = true; break;}//AKQ..
			case 4:{}//AKQJ2 or AKQQ2
			case 5:{}//AKQJJ
		}
		
		boolean lowPair = false;
		combo = identify(handAList);
		switch (combo.split(": ")[0])
		{
			case "Straight Flush" : {}
			case "Four of a Kind" : {}
			case "Full House" : {}
			case "Flush" : {}
			case "Straight" : {}
			case "Three of a Kind" : {vpThreeOfKind(hand, combo.split(": ")[1]); break;}
			case "Two Pair" : {vpTwoPair(hand, combo.split(": ")[1]); break;}
			case "One Pair" : 
			{
				lowPair = "One Pair".equals(combo.split(": ")[0]) && (Integer.valueOf(combo.split(": ")[1]) < 10);
				if (lowPair && highCardCount == 0) {}
				if (lowPair && oneHighCard) vpLowPairOneHigh(hand, combo.split(": ")[1]);
				if (lowPair && twoHighCards) {}
				if (lowPair && threeHighCards) {}
			}
		}
		
		//User Input
		//System.out.print("Discard: ");
		//String inputStr = io.nextLine();
		
		}
		System.out.println("Keep Two Pair: " + countTwoPair );
		System.out.println("Keep Low Pair and One High: " + countLowPairOneHigh);
		System.out.println("Keep Low Pair: " + countLowPair);
		System.out.println("Keep High Card: " + countOneHigh);
	}
	
	static int preFlopOdds(String[] hand)
	{
		boolean suit = false;
		if (hand[0].split("of ").equals(hand[1].split("of "))) suit = true;
		
		boolean pair = false;
		if (hand[0].split(" of").equals(hand[1].split(" of"))) pair = true;
		
		boolean highCard = false;
		if (Integer.valueOf(hand[0].split(" of")[0]) > 9 ||
			Integer.valueOf(hand[1].split(" of")[0]) > 9) highCard = true;
		
		boolean consect = false;
		if (Math.abs(Integer.valueOf(hand[0].split(" of")[0])
			        -Integer.valueOf(hand[1].split(" of")[0]))<5) consect = true;
		
		if (highCard && pair) return 20;//AA KK QQ JJ
		
		if (highCard && consect && suit) return 20;//AK AQ AJ KQ KJ QJ
		
		if (highCard && consect) return 15;//AK AQ AJ KQ KJ QJ
		
		if (pair) return 10;//99 88 77 66 55
		
		if (highCard) return 5;//A9 A8
		
		if (consect && suit) return 5;//98 97 96 
		
		if (consect) return 5;//98 97 96
		
		return 5;//squaDOOSHE
	}
	
	static int flopOdds(String[] hand, String[] shared)
	{
		//AA AA A
		//AK s SSS SS
		//AK QJ9 QJ AK
		//99 922 9
		//A9 A
		//98 s SSS SS
		//98 98
		ArrayList<String> combine = new ArrayList<String>();
		for (int i = 0; i < hand.length; i++) combine.add(hand[i]);
		for (int i = 0; i < shared.length; i++) combine.add(shared[i]);
		String combo = identify(combine);
		
		return 0;
	}
	
	static int turnOdds(String[] hand, String[] shared)
	{
		return 0;
	}
	
	static int riverOdds(String[] hand, String[] shared)
	{
		return 0;
	}
	
	static Scanner io = new Scanner(System.in);
	static int bet(int pot, Player[] players, String[] shared)
	{
		int previousBet = 0;
			
			for (int l = 0; l < players.length; l++)
			{
				System.out.print("P"+l+": "+Arrays.toString(players[l].hand)+" Bet: ");
				
				int currentBet = 0;//Integer.valueOf(io.next());
				
				if (shared == null)//preFlop
				{
					currentBet = preFlopOdds(players[l].hand);
				}
				else
				{
					switch (shared.length)
					{
						case 3 : {currentBet = flopOdds(players[l].hand, shared);break;}
						case 4 : {currentBet = turnOdds(players[l].hand, shared);break;}
						case 5 : {currentBet = riverOdds(players[l].hand, shared);break;}
					}
				}
				
				System.out.println(currentBet);
				
				if (currentBet >= players[l].chips)//all in
				{
					System.out.println("ALL IN");
					previousBet = players[l].chips;
					pot += players[l].chips;
					players[l].chips = 0;
				}
				else if (currentBet == 0)
				{
					//fold condition
				}
				else//call-raise
				{
					previousBet = currentBet;
					pot += currentBet;
					players[l].chips -= currentBet;
				}
			}
			
			System.out.println("Pot: " + pot + "\n");
			return pot;
	}
	
	static void texasHoldEm()
	{
		int numPlayers = 3;
		Player players[] = new Player[numPlayers];
		for (int i = 0; i < players.length; i++) players[i] = new Player();
		
		for (int p = 0 ; p < 100; p++)
		{
			int pot = 0;
			int ante = 5;
			for (int l = 0; l < numPlayers; l++)//ante
			{
				players[l].chips -= ante;
				pot += ante;
			}
			
			/*
			{"2 of Clubs", "2 of Spades", 
			"5 of Clubs", "3 of Spades", 
			 "4 of Clubs", "4 of Spades",
			 "13 of Clubs", "9 of Clubs", "11 of Clubs", "11 of Diamonds", "12 of Clubs"};
			*/
			shuffle();
			
			String[] shared = new String[5];
			int turn = 0;
			int card = 0;
			for (int i = 0; i < numPlayers*2+5; i++)//deal deck
			{
				if (!(turn+1 == players.length && card >= 2))
				{
					if (players[turn].hand[1] != null) {turn++; card = 0;}
					players[turn].hand[card] = draw(); card++;
				}
				else
				{
					shared[card-2] = draw(); card++;
				}
			}
			
			pot = bet(pot, players, null);//first round of betting
			
			System.out.print("Flop: ");
			for (int r = 0; r < 3; r++) System.out.print(shared[r] + ", ");
			System.out.println();
		
			pot = bet(pot, players, Arrays.copyOfRange(shared, 0, 3));//second round of betting
			
			System.out.print("Turn: ");
			for (int r = 0; r < 4; r++) System.out.print(shared[r] + ", ");
			System.out.println();
			
			pot = bet(pot, players, Arrays.copyOfRange(shared, 0, 4));//third round of betting
			
			System.out.print("River: ");
			for (int r = 0; r < 5; r++) System.out.print(shared[r] + ", ");
			System.out.println();
			
			pot = bet(pot, players, shared);//fourth and last round of betting

			String[] results = new String[players.length];
			for (int k = 0; k < numPlayers; k++)
			{
				ArrayList<String> a = new ArrayList<String>();
				for (String c : players[k].hand) a.add(c);
				for (String c : shared) a.add(c);
				results[k] = identify(a);
				System.out.println("Player " + k + ": " + results[k]);//compare players
			}
			int winner = compare(results);
			System.out.println("Player " + winner + " wins pot " + pot);
			for (int l = 0; l < numPlayers; l++) players[l].hand = new String[2]; //resets players hand
			
			System.out.println();
		}
	}
	
	static int compare(String[] results)
	{
		//High Card: 11
		//One Pair: 6
		//Two Pair: 2 14
		//Three of a Kind: 2
		//Straight: 7891011
		//Flush: Spades 13 High
		int rank = 0;
		int highest = 0;
		for (int i = 0; i < results.length; i++)
		{
			switch(results[i].split(":")[0])
			{
				case "Straight Flush" :
				{
					if (7 > rank)
					{
						highest = i; rank = 7;
					}
					else if (7 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1].split(" High")[0].split("s ")[1]) >
							Integer.valueOf(results[highest].split(": ")[1].split(" High")[0].split("s ")[1]))
						{
							highest = i;
						}
					}
					break;
				}
				case "Full House" :
				{
					if (6 > rank)
					{
						highest = i; rank = 6;
					}
					else if (6 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1].split(" over")[0]) >
						Integer.valueOf(results[highest].split(": ")[1].split(" over")[0]))
						{
							highest = i;
						}
					}
					break;
				}
				case "Flush" :
				{
					if (5 > rank)
					{
						highest = i; rank = 5;
					}
					else if (5 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1].split(" High")[0].split("s ")[1]) > 
							Integer.valueOf(results[highest].split(": ")[1].split(" High")[0].split("s ")[1]))
						{
							highest = i;
						}
					}
					break;
				}
				case "Straight" :
				{
					if (4 > rank)
					{
						highest = i; rank = 4;
					}
					else if (4 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1].split(" High")[0].split(" ")[1]) > 
						Integer.valueOf(results[highest].split(": ")[1].split(" High")[0].split(" ")[1]))
						{
							highest = i;
						}
					}
					break;
				}
				case "Three of a Kind" :
				{
					if (3 > rank)
					{
						highest = i; rank = 3;
					}
					else if (3 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1]) > 
						Integer.valueOf(results[highest].split(": ")[1]))
						{
							highest = i;
						}
					}
				}
				case "Two Pair" :
				{
					if (2 > rank)
					{
						highest = i; rank = 2;
					}
					else if (2 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1].split(" ")[1]) >
						Integer.valueOf(results[highest].split(": ")[1].split(" ")[1]))
						{
							highest = i;
						}
					}
				}
				case "One Pair" :
				{
					if (1 > rank)
					{
						highest = i; rank = 1;
					}
					else if (1 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1]) > 
						Integer.valueOf(results[highest].split(": ")[1]))
						{
							highest = i;
						}
					}
				}
				case "High Card" :
				{
					if (0 > rank)
					{
						highest = i; rank = 0;
					}
					else if (0 == rank)
					{
						if (Integer.valueOf(results[i].split(": ")[1]) > 
						Integer.valueOf(results[highest].split(": ")[1]))
						{
							highest = i;
						}
					}
				}
			}
		}
		return highest;
	}
	
	static String identify(ArrayList<String> cards)//full house
	{
		String numbs[] = {"14", "13", "12", "11", "10", "9", "8", "7", "6", "5", "4", "3", "2"};
		String currentHighestCard = "";
		HashMap countSuit = new HashMap();
		HashMap countNum = new HashMap();
		for (String card: cards)
		{
			String suit = card.split(" of ")[1];
			String numb = card.split(" of ")[0];
			
			//High Card
			if (currentHighestCard != "") 
			{
				if (Integer.valueOf(numb) > Integer.valueOf(currentHighestCard)) currentHighestCard = numb;
			}
			else currentHighestCard = numb;
			
			//Pairs
			if (countNum.containsKey(numb)) countNum.put(numb, (Integer)countNum.get(numb)+1);
			else countNum.put(numb, 1);
			
			//Flush
			if (countSuit.containsKey(suit)) countSuit.put(suit, (Integer)countSuit.get(suit)+1);
			else countSuit.put(suit, 1);
		}
		
		int p = 0;
		String pairs[] = new String[3];
		String currentHighest3ofaKind = "";
		for (Object c : countNum.keySet())
		{
			String card = (String) c;
			switch ((Integer)countNum.get(card))
			{
				case 4: return "Four of a Kind: " + card;//Four of a kind
				case 3:
				{
					if (currentHighest3ofaKind != "") 
					{
						if (Integer.valueOf(card) > Integer.valueOf(currentHighest3ofaKind)) currentHighest3ofaKind = card;
					}
					else currentHighest3ofaKind = card;
					break;
				}
				case 2: pairs[p] = card; p++;
			}
		}
		
		boolean flushFlag = false;
		String suit = "";
		//String flushCards = "";
		String currentFlushHigh = "1";
		if (countSuit.containsValue(5) || countSuit.containsValue(6) || countSuit.containsValue(7))
		{
			for (Object s : countSuit.keySet())
			{
				suit = (String) s;
				if ((Integer)countSuit.get(suit) >= 5) 
				{
					for (int i = 0; i < 2; i++)
					{
						String card = cards.get(i);
						if (card.split(" of ")[1].equals(suit)) 
						{
							//flushCards += card.split(" of ")[0];
							if (Integer.valueOf(card.split(" of ")[0]) > Integer.valueOf(currentFlushHigh))
							{
								currentFlushHigh = card.split(" of ")[0];
							}
						}
					}
					flushFlag = true;
				}
 			}
		}
		
		if (currentHighest3ofaKind != "" && p != 0)
		{
			return "Full House: " + currentHighest3ofaKind + " over " + pairs[0];
		}
		
		boolean straightFlag = false;
		String straightCards = "";
		String currentStraightHigh = "";
		int s = 0;
		for (String n : numbs)
		{
			if (countNum.containsKey(n)) s++;
			else s= 0;
			
			if (s == 5)
			{
				for (int i = Integer.valueOf(n); i < Integer.valueOf(n)+5; i++)
				{
					straightCards += i;
					currentStraightHigh = String.valueOf(i);
				}
				straightFlag = true;//return "Straight: " + straight;
			}
		}
		//Arrays.asList(flushCards).containsAll(Arrays.asList(straightCards))
		
		if (straightFlag && flushFlag)
		{
			return "Straight Flush: " + straightCards + " of " + suit + " " + currentStraightHigh + " High";
		}
		else if (flushFlag) return "Flush: of " + suit + " " + currentFlushHigh + " High";
		else if (straightFlag) return "Straight: " + straightCards + " " + currentStraightHigh + " High";
		
		if (currentHighest3ofaKind != "") return "Three of a Kind: " + currentHighest3ofaKind;
		else if (pairs[1] != null)
		{
			int numPairs[] = new int[pairs.length]; 
			int i = 0;
			for (String pair : pairs) 
			{
				if (pair != null) 
					{numPairs[i] = Integer.valueOf(pair);}
				i++;
			}
			Arrays.sort(numPairs);
			return "Two Pair: " + numPairs[1] + " " + numPairs[2];
		}
		else if (pairs[0] != null) return "One Pair: " + pairs[0];
		return "High Card: " + currentHighestCard;
	}
}
