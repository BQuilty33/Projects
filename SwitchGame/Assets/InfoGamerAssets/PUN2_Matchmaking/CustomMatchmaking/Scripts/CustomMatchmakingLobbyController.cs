using Photon.Pun;
using Photon.Realtime;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class CustomMatchmakingLobbyController : MonoBehaviourPunCallbacks
{
    //[SerializeField]
    //private GameObject lobbyConnectButton; //button used for joining a Lobby.
    [SerializeField]
    private GameObject lobbyPanel; //panel for displaying lobby.
    [SerializeField]
    private GameObject mainPanel; //panel for displaying the main menu
    [SerializeField]
    private InputField playerNameInput; //Input field so player can change their NickName

    private string roomName; //string for saving room name
    private int roomSize; //int for saving room size

    private List<RoomInfo> roomListings; //list of current rooms
    [SerializeField]
    private Transform roomsContainer; //container for holding all the room listings
    [SerializeField]
    private GameObject roomListingPrefab; //prefab for displayer each room in the lobby

    public TextMeshProUGUI P1Text;

    public Vector3 GOPO;
    public Vector3 GOPO1;
    public Vector3 GOPO2;

    public override void OnConnectedToMaster() //Callback function for when the first connection is established successfully.
    {
        PhotonNetwork.AutomaticallySyncScene = true; //Makes it so whatever scene the master client has loaded is the scene all other clients will load
        //lobbyConnectButton.SetActive(true); //activate button for connecting to lobby
        //roomListings = new List<RoomInfo>(); //initializing roomListing
        
        //check for player name saved to player prefs
        if(PlayerPrefs.HasKey("NickName"))
        {
            if (PlayerPrefs.GetString("NickName") == "")
            {
                PhotonNetwork.NickName = "Player " + Random.Range(0, 1000); //random player name when not set
            }
            else
            {
                PhotonNetwork.NickName = PlayerPrefs.GetString("NickName"); //get saved player name
            }
        }
        else
        {
            PhotonNetwork.NickName = "Player " + Random.Range(0, 1000); //random player name when not set
        }
        //playerNameInput.text = PhotonNetwork.NickName; //update input field with player name
    }

    public void PlayerNameUpdateInputChanged(string nameInput) //input function for player name. paired to player name input field
    {
        PhotonNetwork.NickName = nameInput;
        PlayerPrefs.SetString("NickName", nameInput);
    }

    public void JoinLobbyOnClick() //Paired to the Delay Start button
    {
        mainPanel.SetActive(false);
        lobbyPanel.SetActive(true);
        PhotonNetwork.JoinLobby(); //First tries to join a lobby
    }

    public override void OnRoomListUpdate(List<RoomInfo> roomList) //Once in lobby this function is called every time there is an update to the room list
    {
        int tempIndex;
        roomListings = new List<RoomInfo>();
        float newx = 0f;
        //float newy = 4270.0f;
        print("dut" + GOPO.y);
        float newy = 0f;
        foreach (RoomInfo room in roomList) //loop through each room in room list
        {
            if (roomListings != null) //try to find existing room listing
            {
                tempIndex = roomListings.FindIndex(ByName(room.Name)); 
            }
            else
            {
                tempIndex = -1;
            }
            if (tempIndex != -1) //remove listing because it has been closed
            {
                roomListings.RemoveAt(tempIndex);
                Destroy(roomsContainer.GetChild(tempIndex).gameObject);
            }
            if (room.PlayerCount > 0) //add room listing because it is new
            {
                roomListings.Add(room);
                ListRoom(room, newx, newy);
            }
            newy -= 40;
        }
    }

    public override void OnJoinedRoom()//called when the local player joins the room
    {
        for(int i = roomsContainer.childCount-1; i == 0; i--)
        {
            Destroy(roomsContainer.GetChild(i).gameObject);
        }
    }


        static System.Predicate<RoomInfo> ByName(string name) //predicate function for seach through room list
    {
        return delegate (RoomInfo room)
        {
            return room.Name == name;
        };
    }
    void ListRoom(RoomInfo room, float x,float y) //displays new room listing for the current room
    {
        if (room.IsOpen && room.IsVisible)
        {
            GameObject tall = GameObject.Find("1904");
            GOPO = tall.transform.position;
            GameObject tall1 = GameObject.Find("1902");
            GOPO2 = tall1.transform.position;
            float sj = GOPO.y - GOPO2.y;
            GameObject tempListing = Instantiate(roomListingPrefab, roomsContainer);
            tempListing.transform.SetParent(GameObject.Find("Scroll View/Viewport/Content").transform, false);
            y = GOPO.y;
            GameObject[] sing99 = GameObject.FindGameObjectsWithTag("earth");
            if (sing99.Length > 1)
            {
                GameObject[] sing9 = GameObject.FindGameObjectsWithTag("earth");
                int flum = sing9.Length - 1;
                GOPO1 = sing9[flum - 1].transform.position;
                print("roslyn" + GOPO1.y);
                y = GOPO1.y - sj;
            }
            tempListing.transform.position = new Vector3(GOPO.x + x, y, 0f);
            RoomButton tempButton = tempListing.GetComponent<RoomButton>();
            tempButton.SetRoom(room.Name, room.MaxPlayers, room.PlayerCount);
        }
    }

    public void RoomNameInputChanged(string nameIn) //input function for changing room name. paired to room name input field
    {
        roomName = nameIn;
    }
    public void OnRoomSizeInputChanged(string sizeIn) //input function for changing room size. paired to room size input field
    {
        roomSize = int.Parse(sizeIn);
    }

    public void CreateRoomOnClick() //function paired to the create room button
    {
        Debug.Log("Creating room now");
        GameObject wear = GameObject.Find("lobbynameT");
        TextMeshProUGUI cameraL = wear.GetComponent<TextMeshProUGUI>();
        string ht = cameraL.text;
        print("eve" + ht);
        RoomOptions roomOps = new RoomOptions() { IsVisible = true, IsOpen = true, MaxPlayers = (byte)4 };
        PhotonNetwork.CreateRoom(ht, roomOps); //attempting to create a new room
        print("island");
    }

    public override void OnCreateRoomFailed(short returnCode, string message) //create room will fail if room already exists
    {
        Debug.Log("Tried to create a new room but failed, there must already be a room with the same name");
    }

    

    public void MatchmakingCancelOnClick() //Paired to the cancel button. Used to go back to the main menu
    {
        mainPanel.SetActive(true);
        lobbyPanel.SetActive(false);
        PhotonNetwork.LeaveLobby();
    }
}
