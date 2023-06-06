﻿using Photon.Pun;
using UnityEngine;
using UnityEngine.UI;
using TMPro;

public class RoomButton : MonoBehaviour {

    [SerializeField]
    private TextMeshProUGUI nameText; //display for room name

    private string roomName; //string for saving room name
    private int playerCount;

    public void JoinRoomOnClick() //paired the button that is the room listing. joins the player a room by its name
    {
        PhotonNetwork.JoinRoom(roomName);
    }

    public void SetRoom(string nameInput, int sizeInput, int countInput) //public function called in CMM lobby contoller for each new room listing created
    {
        roomName = nameInput;
        playerCount = countInput;
        nameText.text = nameInput;
    }
}
